package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.dto.Cart.CartContextDto;
import com.scientia.mercatus.dto.Cart.CartItemDto;
import com.scientia.mercatus.dto.Cart.CartResponseDto;
import com.scientia.mercatus.entity.*;
import com.scientia.mercatus.exception.IllegalQuantity;
import com.scientia.mercatus.repository.CartItemsRepository;
import com.scientia.mercatus.repository.CartRepository;
import com.scientia.mercatus.repository.ProductRepository;
import com.scientia.mercatus.repository.UserRepository;
import com.scientia.mercatus.service.ICartService;
import com.scientia.mercatus.service.SessionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;



@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemsRepository cartItemsRepository;
    private final SessionService sessionService;



    private Cart resolveGuestCart(CartContextDto cartContextDto) {
        String sessionId = cartContextDto.getSessionId();
        if (sessionId == null) {
            throw new IllegalStateException("Session id is null");
        }
        return cartRepository.findBySessionId(sessionId).orElseGet(
                () -> createGuestCart(sessionId)
        );
    }

    private Cart resolveUserCart(CartContextDto cartContextDto) {
        Cart userCart = cartRepository.
                findByUser_UserId(cartContextDto.getUserId()).orElse(null);
        User user = userRepository.findByUserId(cartContextDto.getUserId());
        String sessionId = cartContextDto.getSessionId();
        if  (sessionId == null) {
            throw new IllegalStateException("Session id is null");
        }
        Cart guestCart = cartRepository.findBySessionId(sessionId).orElse(null);
        if (userCart == null && guestCart == null) {
            return createUserCart(user);
        }
        if (guestCart == null) {
            return userCart;
        }
        if (userCart == null) {
            Cart attachCart =  attachGuestCartToUser(guestCart,  user);
            sessionService.revokeSession(sessionId);
            return attachCart;
        }

        mergeGuestToUserCart(guestCart, userCart);
        sessionService.revokeSession(sessionId);
        cartRepository.delete(guestCart);
        return  cartRepository.save(userCart);
    }



    @Override
    public Cart resolveCart(CartContextDto cartContextDto) {

        if (cartContextDto.getUserId() == null) {
            return resolveGuestCart(cartContextDto);
        }
        return resolveUserCart(cartContextDto);

    }

    private Cart createGuestCart(String sessionId) {
        Cart newCart = new Cart();
        newCart.setSessionId(sessionId);
        cartRepository.save(newCart);
        return newCart;
    }

    private Cart createUserCart(User currentUser) {
        Cart newCart = new Cart();
        newCart.setUser(currentUser);
        cartRepository.save(newCart);
        return newCart;
    }

    private Cart attachGuestCartToUser(Cart guestCart, User user) {
        guestCart.setUser(user);
        guestCart.setSessionId(null);
        return cartRepository.save(guestCart);
    }

    private void mergeGuestToUserCart(Cart guestCart, Cart userCart) {
        Map<Long, CartItem> cartItems =  userCart.getCartItems().stream().collect(Collectors.toMap(
                item -> item.getProduct().getProductId(),
                item -> item
        ));
        for (CartItem guestItem: guestCart.getCartItems()) {
            Long productId = guestItem.getProduct().getProductId();
            if (cartItems.containsKey(productId)) {
                CartItem cartItem = cartItems.get(productId);
                cartItem.setQuantity(cartItem.getQuantity() + (guestItem.getQuantity()));
            }
            else {
                guestItem.setCart(userCart);
                userCart.getCartItems().add(guestItem);
                cartItems.put(productId, guestItem);
            }
        }

    }

    @Override
    public void addToCart(Cart currentCart, Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalQuantity("Quantity must be greater than 0");
        }

        if (productId == null) {
            throw new IllegalArgumentException("Product id cannot be null");
        }
        Product currentProduct = productRepository.findByProductId(productId).orElseThrow(()
                -> new RuntimeException("Product not found"));
        Optional<CartItem> cartItemOptional = cartItemsRepository.findByCartAndProduct(currentCart, currentProduct);
        if (cartItemOptional.isPresent()) {
            CartItem currentItem = cartItemOptional.get();
            currentItem.setQuantity(currentItem.getQuantity() + quantity);
            cartItemsRepository.save(currentItem);
            return;
        }
        CartItem cartItem = new CartItem();
        cartItem.setProduct(currentProduct);
        cartItem.setQuantity(quantity);
        cartItem.setCart(currentCart);
        cartItemsRepository.save(cartItem);
    }

    @Override
    public void removeFromCart(Cart currentCart, Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product id cannot be null");
        }
        Product currentProduct = productRepository.findByProductId(productId).
                orElseThrow(()-> new RuntimeException("Product not found: " + productId));
        Optional<CartItem> cartItemOptional = cartItemsRepository.findByCartAndProduct(currentCart, currentProduct);
        cartItemOptional.ifPresent(cartItemsRepository::delete);
    }

    @Override
    public void clearCart(Cart currentCart) {
        cartItemsRepository.deleteByCart(currentCart);
    }


    @Override
    public void updateQuantity(Cart currentCart, Long productId, Integer quantity) {
        if (quantity == null || quantity  < 0) {
            throw new IllegalQuantity("Quantity must be greater than 0");
        }
        if (productId == null) {
            throw new IllegalArgumentException("Product id cannot be null");
        }
        Product currentProduct = productRepository.findByProductId(productId).
                orElseThrow(()-> new RuntimeException("Product not found: " + productId));
        Optional<CartItem> currentCartItemOptional = cartItemsRepository.
                findByCartAndProduct(currentCart, currentProduct);
        if (currentCartItemOptional.isEmpty()) {
            return;
        }

        CartItem item = currentCartItemOptional.get();
        if (quantity == 0) {
            cartItemsRepository.delete(item);
            return;
        }
        item.setQuantity(quantity);
        cartItemsRepository.save(item);
    }


    @Override
    public CartResponseDto getCartDetails(Cart cart) {
        Set<CartItemDto> cartItemList = new LinkedHashSet<>();
        List<CartItem> items = cartItemsRepository.findByCartWithProduct(cart);
        int totalCount = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem item : items) {
            BigDecimal totalItemPrice = BigDecimal.valueOf(item.getQuantity()).multiply(item.getProduct().getPrice());
            CartItemDto cartItemDto = new CartItemDto(
                    item.getCartItemId(),
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getProduct().getPrice(),
                    totalItemPrice
            );
            cartItemList.add(cartItemDto);
            totalPrice =  totalPrice.add(totalItemPrice);
            totalCount += item.getQuantity().intValue();
        }
        return new CartResponseDto(cartItemList, totalCount, totalPrice);
    }

    @Override
    public Cart lockCartForCheckout(Long cartId) {
        return cartRepository.findActiveCartForUpdate(cartId).orElseThrow(() ->
                new IllegalStateException("Cart already checked out or inactive"));
    }
}

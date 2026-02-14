package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.dto.Cart.CartContextDto;
import com.scientia.mercatus.dto.Cart.CartItemDto;
import com.scientia.mercatus.dto.Cart.CartResponseDto;
import com.scientia.mercatus.entity.*;
import com.scientia.mercatus.exception.IllegalQuantity;
import com.scientia.mercatus.repository.CartItemsRepository;
import com.scientia.mercatus.repository.CartRepository;
import com.scientia.mercatus.repository.UserRepository;
import com.scientia.mercatus.service.ICartService;
import com.scientia.mercatus.service.IProductService;
import com.scientia.mercatus.service.ISessionService;
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
    private final CartItemsRepository cartItemsRepository;
    private final ISessionService ISessionService;
    private final IProductService productService;



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
        if (userCart == null && guestCart == null) { // if guest cart and user cart both absent
            return createUserCart(user);
        }
        if (guestCart == null) { // if guest cart absent, but user cart may be present
            return userCart;
        }
        if (userCart == null) { // if user cart absent, but guest cart may be present
            Cart attachCart =  attachGuestCartToUser(guestCart,  user);
            ISessionService.revokeSession(sessionId);
            return attachCart;
        }

        mergeGuestToUserCart(guestCart, userCart);// if both cart are present
        ISessionService.revokeSession(sessionId);
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
    public void addToCart(CartContextDto ctxDto, Long productId, Integer quantity) {
        Cart currentCart = resolveCart(ctxDto);
        if (quantity == null || quantity <= 0) {
            throw new IllegalQuantity("Quantity must be greater than 0");
        }

        if (productId == null) {
            throw new IllegalArgumentException("Product id cannot be null");
        }
        Product currentProduct = productService.getActiveProduct(productId);
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
    public void removeFromCart(CartContextDto ctxDto, Long productId) {
        Cart currentCart = resolveCart(ctxDto);
        if (productId == null) {
            throw new IllegalArgumentException("Product id cannot be null");
        }
        cartItemsRepository
                .findByCartAndProduct_ProductId(currentCart, productId)
                .ifPresent(cartItemsRepository::delete);
    }

    @Override
    public void clearCart(CartContextDto ctxDto) {
        Cart currentCart = resolveCart(ctxDto);
        cartItemsRepository.deleteByCart(currentCart);
    }


    @Override
    public void updateQuantity(CartContextDto ctxDto, Long productId, Integer quantity) {
        Cart currentCart = resolveCart(ctxDto);
        if (quantity == null || quantity  < 0) {
            throw new IllegalQuantity("Quantity must be greater than 0");
        }
        if (productId == null) {
            throw new IllegalArgumentException("Product id cannot be null");
        }
        CartItem item = cartItemsRepository
                .findByCartAndProduct_ProductId(currentCart, productId).orElse(null);

        if (item == null) {
            return;
        }
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
            Product product = productService.getActiveProduct(item.getProduct().getProductId());
            BigDecimal itemPrice = product.getPrice();
            BigDecimal totalItemPrice = BigDecimal.valueOf(item.getQuantity()).multiply(item.getProduct().getPrice());
            CartItemDto cartItemDto = new CartItemDto(
                    item.getCartItemId(),
                    product.getName(),
                    item.getQuantity(),
                    itemPrice,
                    totalItemPrice
            );
            cartItemList.add(cartItemDto);
            totalPrice =  totalPrice.add(totalItemPrice);
            totalCount += item.getQuantity();
        }
        return new CartResponseDto(cartItemList, totalCount, totalPrice);
    }

    @Override
    public Cart lockCartForCheckout(Long cartId) {
        return cartRepository.findActiveCartForUpdate(cartId).orElseThrow(() ->
                new IllegalStateException("Cart already checked out or inactive"));
    }
}

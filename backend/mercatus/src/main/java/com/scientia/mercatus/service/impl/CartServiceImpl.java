package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.entity.*;
import com.scientia.mercatus.exception.CartNotFoundException;
import com.scientia.mercatus.exception.IllegalQuantity;
import com.scientia.mercatus.repository.CartItemsRepository;
import com.scientia.mercatus.repository.CartRepository;
import com.scientia.mercatus.repository.ProductRepository;
import com.scientia.mercatus.repository.UserRepository;
import com.scientia.mercatus.service.ICartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

enum Profile {
    GUEST,
    USER
}

@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemsRepository cartItemsRepository;
    @Override
    public Cart createCart(String sessionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticationState = isAuthenticated(authentication);

        Profile activeProfile = authenticationState ? Profile.USER : Profile.GUEST;
        Long userId = extractUserId(authentication);

        Optional<Cart> guestCart = cartRepository.findBySessionId(sessionId);
        Optional<Cart> userCart = userId == null ? Optional.empty() : cartRepository.findByUser_UserId(userId);

        Cart activeCart = determineActiveCart(activeProfile, userId, sessionId,
                guestCart.orElse(null), userCart.orElse(null));

        persistCartChanges(activeCart, guestCart.isPresent() && userCart.isPresent());

        return activeCart;
    }


    private Cart determineActiveCart(Profile activeProfile, Long userId, String sessionId, Cart guestCart, Cart userCart) {
        boolean hasUserCart = userCart != null;
        boolean hasGuestCart = guestCart != null;

        if (!hasUserCart && !hasGuestCart) {
            return resolveCartCreation(activeProfile, sessionId, userId);
        }
        if (hasGuestCart && !hasUserCart) {
            if (activeProfile == Profile.USER && userId != null) {
                return convertGuestCartToUserCart(guestCart, userId);
            }
        }
        if (hasUserCart && !hasGuestCart) {
            return userCart;
        }
        return mergeUserGuestCart(guestCart, userCart);
    }

    private Long extractUserId(Authentication authentication) {
        var principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return ((User) principal).getUserId();
        }
        return null;
    }

    private Cart convertGuestCartToUserCart(Cart guestCart, Long userId) {
        User user = userRepository.findByUserId(userId);
        guestCart.setUser(user);
        guestCart.setSessionId(null);
        return guestCart;
    }

    private Cart resolveCartCreation(Profile profileType, String sessionId, Long userId) {
        Cart cart = new Cart();
        cart.setSessionId(sessionId);
        if (userId != null) {
            User currentUser = userRepository.findByUserId(userId);
            cart.setUser(currentUser);
        }
        return cart;
    }

    private Cart mergeUserGuestCart (Cart guestCart, Cart userCart) {
        Map<Long, CartItems> cartStoredProducts = new HashMap<>();
        for(CartItems item : userCart.getCartItems()) {
            cartStoredProducts.put(item.getProduct().getProductId(), item);
        }
        for (CartItems item : guestCart.getCartItems()) {
            Long productId = item.getProduct().getProductId();
            BigDecimal guestCartQuantity = item.getQuantity() != null ? item.getQuantity() : BigDecimal.ZERO;
            if (cartStoredProducts.containsKey(productId)) {

                CartItems userCartItems= cartStoredProducts.get(productId);
                BigDecimal userCartItemsQuantity = userCartItems.getQuantity() != null ? userCartItems.getQuantity() : BigDecimal.ZERO;

                BigDecimal currentQuantity = userCartItemsQuantity.add(guestCartQuantity);
                userCartItems.setQuantity(currentQuantity);

            }
            else {
                item.setCart(userCart);
                userCart.getCartItems().add(item);
                cartStoredProducts.put(productId, item);
            }
        }
        return userCart;

    }

    private boolean isAuthenticated(Authentication authentication) {
        return  authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken);

    }

    protected void persistCartChanges(Cart activeCart, boolean deleteGuest) {
        cartRepository.save(activeCart);
        if (deleteGuest && activeCart.getUser() != null && activeCart.getSessionId() != null) {
            cartRepository.deleteBySessionId(activeCart.getSessionId());
        }
    }

    @Override
    public void addProductToCart(long cartId, long productId, BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <=0) {
            throw new IllegalQuantity("Quantity must be greater than 0");
        }
        Cart currentCart = cartRepository.findByCartId(cartId).orElseThrow(() -> new CartNotFoundException("Cart not found"));
        Product currentProduct = productRepository.findByProductId(productId).orElseThrow(NoSuchElementException::new);
        Set<CartItems> currentCartItems = currentCart.getCartItems();
        for (CartItems item : currentCartItems) {
            if (item.getProduct().getProductId().equals(productId)) {
                BigDecimal currentQuantity = item.getQuantity();
                currentQuantity = currentQuantity.add(quantity);
                item.setQuantity(currentQuantity);
                cartRepository.save(currentCart);
                return;
            }
        }
        CartItems cartItem = new CartItems();
        cartItem.setProduct(currentProduct);
        cartItem.setQuantity(quantity);
        cartItem.setCart(currentCart);
        currentCartItems.add(cartItem);
        cartRepository.save(currentCart);
    }




    @Override
    public void removeProductFromCart(long cartId, long productId) {
        Product product = productRepository.findByProductId(productId).orElseThrow(NoSuchElementException::new);
        Cart cart = cartRepository.findByCartId(cartId).orElseThrow(() ->
                new CartNotFoundException("Cart not found"));
        CartItems item = cart.getCartItems().
                stream().filter(i -> i.getProduct().getProductId().equals(productId))
                .findFirst().orElse(null);
        if (item != null) {
            cart.getCartItems().remove(item);
        }

    }

    @Override
    public void updateProductQuantity(long cartId, long productId, double quantity) {
            BigDecimal currentQuantity = BigDecimal.valueOf(quantity);
            if (currentQuantity.compareTo(BigDecimal.ZERO) < 0) { // First case : quantity < 0
                throw new IllegalQuantity("Quantity must be greater than 0");
            }
            Cart cart = cartRepository.findByCartId(cartId)
                    .orElseThrow(() -> new CartNotFoundException("Cart not found"));
            CartItems item = cart.getCartItems().stream()
                    .filter(i -> i.getProduct().getProductId().equals(productId))
                    .findFirst().orElse(null);
            if (item == null) {
                return;
            }
            if (currentQuantity.compareTo(BigDecimal.ZERO) == 0) { //Second case: quantity = 0
                cart.getCartItems().remove(item);
                return;
            }
            item.setQuantity(currentQuantity); //Final case: update the quantity

    }

    @Override
    public Cart getCart(String sessionId) {
        return createCart(sessionId);
    }

    @Override
    public void clearCart(long cartId) {
        Cart cart = cartRepository.
                findByCartId(cartId).orElseThrow(() -> new CartNotFoundException("Cart not found"));
        cart.getCartItems().clear();
    }



}

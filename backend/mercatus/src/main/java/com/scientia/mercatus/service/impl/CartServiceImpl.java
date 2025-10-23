package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.entity.Cart;
import com.scientia.mercatus.entity.CartItems;
import com.scientia.mercatus.entity.Orders;
import com.scientia.mercatus.entity.User;
import com.scientia.mercatus.repository.CartRepository;
import com.scientia.mercatus.service.ICartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {

    private final CartRepository cartRepository;
    @Override
    public Cart createCart(String sessionId) {
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       var cart = cartRepository.findBySessionId(sessionId);//check if guest cart is available
        if ((authentication == null || !authentication.isAuthenticated()) &&  cart.isEmpty()) {
            Cart newCart = new Cart();
            newCart.setSessionId(sessionId);
            cartRepository.save(newCart);
            return newCart;
        }
        if ((authentication == null || !authentication.isAuthenticated()) && !cart.isEmpty()) {
            return cart.get();
        }
        if (authentication != null && authentication.isAuthenticated()) {
            var user = (User)authentication.getPrincipal();
            var existingUserCart = cartRepository.findByUser_UserId(user.getUserId());
            if (existingUserCart.isEmpty()) {
                Cart newCart = new Cart();
                newCart.setUser(user);
                cartRepository.save(newCart);
                return newCart;
            }
            return existingUserCart.get();
        }
        var user = (User)authentication.getPrincipal();
        var userCart = cartRepository.findByUser_UserId(user.getUserId());
        return mergeUserGuestCart(cart.get(), userCart.get());
    }

    private Cart mergeUserGuestCart (Cart guestCart, Cart userCart) {
        for (CartItems item : guestCart.getCartItems())
            userCart.
    }

    @Override
    public void addProductToCart(long cartId, long productId, double quantity) {

    }

    @Override
    public void removeProductFromCart(long cartId, long productId) {

    }

    @Override
    public void updateProductQuantity(long cartId, long productId, double quantity) {

    }

    @Override
    public Cart getCart() {
        return null;
    }

    @Override
    public void clearCart(long cartId) {

    }

    @Override
    public Orders checkout(long cartId) {
        return null;
    }
}

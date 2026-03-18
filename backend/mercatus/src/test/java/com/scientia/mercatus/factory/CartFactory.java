package com.scientia.mercatus.factory;

import com.scientia.mercatus.entity.Cart;

import com.scientia.mercatus.entity.User;

import java.util.UUID;


public class CartFactory {

    public static Cart create(User user) {
        Cart cart = new Cart();
        cart.setSessionId(UUID.randomUUID().toString());
        cart.setUser(user);
        return cart;
    }
}

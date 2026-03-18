package com.scientia.mercatus.factory;

import com.scientia.mercatus.entity.Cart;
import com.scientia.mercatus.entity.CartItem;
import com.scientia.mercatus.entity.Product;

public class CartItemFactory {

    public static CartItem create(Product product, Cart cart, Integer quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setCart(cart);
        cart.getCartItems().add(cartItem);
        return cartItem;
    }
}

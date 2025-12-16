package com.scientia.mercatus.service;

import com.scientia.mercatus.entity.Cart;
import com.scientia.mercatus.entity.Orders;

import java.math.BigDecimal;

public interface ICartService {
    public Cart createCart(String sessionId);
    public void addProductToCart(long cartId, long productId, BigDecimal quantity);
    public void removeProductFromCart(long cartId, long productId);
    public void updateProductQuantity(long cartId, long productId, double quantity);
    public Cart getCart(String sessionId);
    public void clearCart(long cartId);
}

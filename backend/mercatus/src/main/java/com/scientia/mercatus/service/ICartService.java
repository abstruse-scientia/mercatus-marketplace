package com.scientia.mercatus.service;

import com.scientia.mercatus.dto.CartContextDto;
import com.scientia.mercatus.dto.CartResponseDto;
import com.scientia.mercatus.entity.Cart;

import java.math.BigDecimal;

public interface ICartService {
    Cart resolveCart(CartContextDto cartContext);
    void addToCart(Cart currentCart, Long productId, Integer quantity);
    void removeFromCart(Cart currentCart, Long productId);
    void clearCart(Cart currentCart);
    void updateQuantity(Cart currentCart, Long productId, Integer quantity);
    CartResponseDto getCartDetails(Cart Cart);
    Cart lockCartForCheckout(Long cartId);

}

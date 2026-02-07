package com.scientia.mercatus.service;

import com.scientia.mercatus.dto.Cart.CartContextDto;
import com.scientia.mercatus.dto.Cart.CartResponseDto;
import com.scientia.mercatus.entity.Cart;

public interface ICartService {
    Cart resolveCart(CartContextDto cartContext);
    void addToCart(CartContextDto ctxDto, Long productId, Integer quantity);
    void removeFromCart(CartContextDto ctx, Long productId);
    void clearCart(CartContextDto ctx);
    void updateQuantity(CartContextDto ctx, Long productId, Integer quantity);
    CartResponseDto getCartDetails(Cart Cart);
    Cart lockCartForCheckout(Long cartId);
}

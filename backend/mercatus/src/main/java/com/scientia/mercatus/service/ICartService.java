package com.scientia.mercatus.service;

import com.scientia.mercatus.dto.Cart.CartContextDto;
import com.scientia.mercatus.dto.Cart.CartResponseDto;
import com.scientia.mercatus.entity.Cart;

public interface ICartService {
    Cart resolveCart(CartContextDto cartContext);
    void addToCart(Cart currentCart, Long productId, Integer quantity);
    void removeFromCart(Cart currentCart, Long productId);
    void clearCart(Cart currentCart);
    void updateQuantity(Cart currentCart, Long productId, Integer quantity);
    CartResponseDto getCartDetails(Cart Cart);
    Cart lockCartForCheckout(Long cartId);

}

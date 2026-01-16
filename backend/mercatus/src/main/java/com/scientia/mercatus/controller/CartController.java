package com.scientia.mercatus.controller;

import com.scientia.mercatus.dto.*;
import com.scientia.mercatus.entity.Cart;

import com.scientia.mercatus.service.impl.CartServiceImpl;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartServiceImpl cartService;


    @GetMapping()
    public ResponseEntity<CartResponseDto> getCart(CartContextDto cartContextDto) {
        Cart currentCart = getCurrentCart(cartContextDto);
        return ResponseEntity.ok(cartService.getCartDetails(currentCart));
    }

    @PostMapping("/items")
    public ResponseEntity<Void> addItemToCart(@RequestBody AddToCartRequestDto addToCartRequestDto,
                                              CartContextDto cartContextDto) {

        Cart currentCart = getCurrentCart(cartContextDto);
        cartService.addToCart(currentCart,
                addToCartRequestDto.productId(),
                addToCartRequestDto.quantity());
        return ResponseEntity.notFound().build();
    }


    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Long productId,
                                                   CartContextDto cartContextDto) {

        Cart currentCart = getCurrentCart(cartContextDto);
        cartService.removeFromCart(currentCart, productId);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/items")
    public ResponseEntity<Void> updateQuantity(@RequestBody
                                               UpdateQuantityRequestDto updateQuantityRequestDto,
                                               CartContextDto cartContextDto
    ) {
        Cart currentCart = getCurrentCart(cartContextDto);
        cartService.updateQuantity(currentCart,
                updateQuantityRequestDto.productId(),
                updateQuantityRequestDto.quantity());
        return ResponseEntity.ok().build();

    }


    @DeleteMapping
    public ResponseEntity<Void> clearCart(CartContextDto cartContextDto) {
        Cart currentCart = getCurrentCart(cartContextDto);
        cartService.clearCart(currentCart);
        return ResponseEntity.noContent().build();
    }


    private Cart getCurrentCart(CartContextDto cartContextDto) {
        return  cartService.resolveCart(cartContextDto);
    }

}











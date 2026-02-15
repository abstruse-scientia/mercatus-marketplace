package com.scientia.mercatus.controller;

import com.scientia.mercatus.dto.Auth.UpdateQuantityRequestDto;
import com.scientia.mercatus.dto.Cart.AddToCartRequestDto;
import com.scientia.mercatus.dto.Cart.CartContextDto;
import com.scientia.mercatus.dto.Cart.CartResponseDto;
import com.scientia.mercatus.entity.Cart;

import com.scientia.mercatus.service.impl.CartServiceImpl;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
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


        cartService.addToCart(cartContextDto,
                addToCartRequestDto.productId(),
                addToCartRequestDto.quantity());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Long productId,
                                                   CartContextDto cartContextDto) {

        cartService.removeFromCart(cartContextDto, productId);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/items")
    public ResponseEntity<Void> updateQuantity(@RequestBody
                                                   UpdateQuantityRequestDto updateQuantityRequestDto,
                                               CartContextDto cartContextDto
    ) {
        cartService.updateQuantity(cartContextDto,
                updateQuantityRequestDto.productId(),
                updateQuantityRequestDto.quantity());
        return ResponseEntity.ok().build();

    }


    @DeleteMapping
    public ResponseEntity<Void> clearCart(CartContextDto cartContextDto) {
        cartService.clearCart(cartContextDto);
        return ResponseEntity.noContent().build();
    }


    private Cart getCurrentCart(CartContextDto cartContextDto) {
        return  cartService.resolveCart(cartContextDto);
    }

}











package com.scientia.mercatus.dto.Cart;



import java.math.BigDecimal;

public record CartItemDto(Long id, String name, Integer quantity, BigDecimal unitPrice, BigDecimal totalItemsPrice) {
}

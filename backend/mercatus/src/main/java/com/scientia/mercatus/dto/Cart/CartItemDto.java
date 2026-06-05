package com.scientia.mercatus.dto.Cart;



import java.math.BigDecimal;

public record CartItemDto(Long id, Long productId, String name, Integer quantity, BigDecimal unitPrice, BigDecimal totalItemsPrice, String primaryImageUrl, String categoryName) {
}

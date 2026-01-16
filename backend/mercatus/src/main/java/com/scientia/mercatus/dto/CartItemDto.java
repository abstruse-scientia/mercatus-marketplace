package com.scientia.mercatus.dto;



import java.math.BigDecimal;

public record CartItemDto(Long id, String name, BigDecimal quantity, BigDecimal unitPrice, BigDecimal totalItemsPrice) {
}

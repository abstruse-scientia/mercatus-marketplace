package com.scientia.mercatus.dto;

import com.scientia.mercatus.entity.CartItems;
import com.scientia.mercatus.entity.Product;

import java.math.BigDecimal;

public record CartItemDto(Long id, String name, BigDecimal quantity, BigDecimal unitPrice, BigDecimal totalItemsPrice) {
}

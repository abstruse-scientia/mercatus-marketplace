package com.scientia.mercatus.dto.Order;

import java.math.BigDecimal;

public record OrderItemDto(Long productId, int quantity, BigDecimal unitPrice, String productName) {
}

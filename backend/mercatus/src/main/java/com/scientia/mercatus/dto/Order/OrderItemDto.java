package com.scientia.mercatus.dto.Order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderItemDto(@NotNull(message = "Product Id required.")Long productId,
                           int quantity,
                           @Positive(message = "Positive price required") BigDecimal unitPrice,
                           @NotNull(message = "Product name required.") String productName) {
}

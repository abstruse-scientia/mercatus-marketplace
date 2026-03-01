package com.scientia.mercatus.dto.Cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddToCartRequestDto(
        @NotNull(message = "Product Id required.")Long productId,
        @Positive(message = "Quantity in positive required.") Integer quantity) {
}

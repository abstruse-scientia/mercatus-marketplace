package com.scientia.mercatus.dto.Auth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateQuantityRequestDto(
        @NotNull(message = "Product id required.") Long productId,
        @Positive(message = "Price must be above 0.") Integer quantity) {
}

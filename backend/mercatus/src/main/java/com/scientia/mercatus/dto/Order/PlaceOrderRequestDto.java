package com.scientia.mercatus.dto.Order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlaceOrderRequestDto(
        @NotBlank(message = "Order reference required") String orderReference,
        @NotNull(message = "Address Id required") Long addressId
) {
}

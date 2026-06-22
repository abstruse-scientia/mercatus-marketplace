package com.scientia.mercatus.dto.Order.Admin;

import com.scientia.mercatus.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusDto(
        @NotNull OrderStatus status
        ) {
}

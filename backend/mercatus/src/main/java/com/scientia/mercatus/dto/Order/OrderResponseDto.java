package com.scientia.mercatus.dto.Order;

import com.scientia.mercatus.entity.OrderStatus;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@AllArgsConstructor
public class OrderResponseDto {
    private Long orderId;
    private BigDecimal orderTotal;
    private OrderStatus orderStatus;
    private Instant placedAt;
    private List<OrderItemDto> items;
}

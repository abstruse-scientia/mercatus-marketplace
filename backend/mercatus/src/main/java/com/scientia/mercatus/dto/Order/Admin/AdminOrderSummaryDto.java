package com.scientia.mercatus.dto.Order.Admin;

import com.scientia.mercatus.entity.OrderItem;
import com.scientia.mercatus.entity.OrderPaymentStatus;
import com.scientia.mercatus.entity.OrderStatus;

import java.time.Instant;
import java.util.Set;

public record AdminOrderSummaryDto(
         String userName,
         String userEmail,
         String orderReference,
         Long orderId,
         Set<OrderItem> orderItems,
         OrderStatus status,
         OrderPaymentStatus paymentStatus,
         Instant createdAt

) {
}

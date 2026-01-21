package com.scientia.mercatus.dto;

import com.scientia.mercatus.entity.OrderStatus;
import com.scientia.mercatus.entity.PaymentStatus;


import java.math.BigDecimal;
import java.time.Instant;

public class OrderSummaryDto {
    Long id;
    BigDecimal totalAmount;
    PaymentStatus paymentStatus;
    OrderStatus orderStatus;
    String orderReference;
    Instant createdAt;

    public OrderSummaryDto(Long id,  BigDecimal totalAmount, PaymentStatus paymentStatus,
                           OrderStatus orderStatus, String orderReference, Instant createdAt) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.orderStatus = orderStatus;
        this.orderReference = orderReference;
        this.createdAt = createdAt;

    }
}

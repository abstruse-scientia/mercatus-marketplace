package com.scientia.mercatus.dto.Order;

import com.scientia.mercatus.entity.OrderStatus;
import com.scientia.mercatus.entity.OrderPaymentStatus;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderSummaryDto {
    Long id;
    BigDecimal totalAmount;
    OrderPaymentStatus orderPaymentStatus;
    OrderStatus orderStatus;
    String orderReference;
    Instant createdAt;
    List<OrderItemSummaryDto> orderSummaryList;

    public OrderSummaryDto(Long id,  BigDecimal totalAmount, OrderPaymentStatus orderPaymentStatus,
                           OrderStatus orderStatus, String orderReference, Instant createdAt, List<OrderItemSummaryDto> orderSummaryList) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.orderPaymentStatus = orderPaymentStatus;
        this.orderStatus = orderStatus;
        this.orderReference = orderReference;
        this.createdAt = createdAt;
        this.orderSummaryList = orderSummaryList;
    }
}

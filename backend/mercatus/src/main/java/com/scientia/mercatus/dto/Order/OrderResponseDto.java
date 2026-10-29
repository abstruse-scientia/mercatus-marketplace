package com.scientia.mercatus.dto.Order;

import com.scientia.mercatus.entity.AddressSnapshot;
import com.scientia.mercatus.entity.OrderPaymentStatus;
import com.scientia.mercatus.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    private Long orderId;
    private String orderReference;
    private OrderPaymentStatus orderPaymentStatus;
    private BigDecimal orderTotal;
    private OrderStatus orderStatus;
    private Instant placedAt;
    private List<OrderItemDto> items;
    private AddressSnapshot addressSnapshot;
    
}

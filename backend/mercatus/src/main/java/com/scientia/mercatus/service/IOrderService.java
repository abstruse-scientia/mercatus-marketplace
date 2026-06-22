package com.scientia.mercatus.service;
import com.scientia.mercatus.dto.Order.OrderResponseDto;
import com.scientia.mercatus.dto.Order.OrderSummaryDto;
import com.scientia.mercatus.dto.Payment.PaymentInitiationResultDto;
import com.scientia.mercatus.entity.AddressSnapshot;
import com.scientia.mercatus.entity.Order;
import com.scientia.mercatus.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IOrderService {
    void cancelOrder(Long orderId, Long userId);
    Page<OrderSummaryDto> getOrdersForUser(Long userId, Pageable pageable);
    Page<OrderSummaryDto> getOrdersForUser(Long userId, OrderStatus status, Pageable pageable);
    PaymentInitiationResultDto initiatePayment(Long orderId, Long userId);
    Optional<Order> getExistingOrder(Long userId, String orderReference);
    Order makeOrderSkeleton(Long userId, String orderReference, AddressSnapshot addressSnapshot);
    OrderResponseDto getOrderById(Long orderId, Long userId);
    Page<Order> listAllOrders(OrderStatus status, Pageable pageable);
    Order getOrderByOrderId(Long orderId);
    Order updateOrderStatus(Long orderId, OrderStatus orderStatus);
}

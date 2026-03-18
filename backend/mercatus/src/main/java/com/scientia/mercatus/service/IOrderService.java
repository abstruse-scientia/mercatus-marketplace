package com.scientia.mercatus.service;
import com.razorpay.RazorpayException;
import com.scientia.mercatus.dto.Order.OrderSummaryDto;
import com.scientia.mercatus.dto.Order.PlaceOrderRequestDto;
import com.scientia.mercatus.dto.Payment.PaymentInitiationResultDto;
import com.scientia.mercatus.entity.AddressSnapshot;
import com.scientia.mercatus.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IOrderService {
    void cancelOrder(Long orderId, Long userId);
    Page<OrderSummaryDto> getOrdersForUser(Long userId, Pageable pageable);
    PaymentInitiationResultDto initiatePayment(Long orderId, Long userId);
    Optional<Order> getExistingOrder(Long userId, String orderReference);
    Order makeOrderSkeleton(Long userId, String orderReference, AddressSnapshot addressSnapshot);
}

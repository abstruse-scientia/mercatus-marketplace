package com.scientia.mercatus.service;
import com.scientia.mercatus.dto.Order.OrderSummaryDto;
import com.scientia.mercatus.dto.Payment.PaymentIntentResultDto;
import com.scientia.mercatus.entity.Order;
import com.scientia.mercatus.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IOrderService {
    Order placeOrder(String sessionId, Long userId, String orderReference);
    void cancelOrder(Long orderId, Long userId);
    Page<OrderSummaryDto> getOrdersForUser(Long userId, Pageable pageable);
    void handlePaymentSuccess(String providerPaymentId);
    void handlePaymentFailure(String providerPaymentId);
    PaymentIntentResultDto initiatePayment(Long orderId, Long userId);
}

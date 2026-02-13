package com.scientia.mercatus.service;
import com.razorpay.RazorpayException;
import com.scientia.mercatus.dto.Order.OrderSummaryDto;
import com.scientia.mercatus.dto.Payment.PaymentInitiationResultDto;
import com.scientia.mercatus.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IOrderService {
    Order placeOrder(String sessionId, Long userId, String orderReference);
    void cancelOrder(Long orderId, Long userId);
    Page<OrderSummaryDto> getOrdersForUser(Long userId, Pageable pageable);
    void markPaid(String orderReference);
    void finalizePaidOrder(String orderReference);
    void markPaymentFail(String orderReference);
    void cancelFailedOrder(String orderReference);
    PaymentInitiationResultDto initiatePayment(Long orderId, Long userId);
}

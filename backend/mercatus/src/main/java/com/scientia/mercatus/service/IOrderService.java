package com.scientia.mercatus.service;
import com.scientia.mercatus.dto.OrderSummaryDto;
import com.scientia.mercatus.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IOrderService {
    Order placeOrder(String sessionId, Long userId, String orderReference);
    void cancelOrder(Long orderId, Long userId);
    Page<OrderSummaryDto> getOrdersForUser(Long userId, Pageable pageable);
}

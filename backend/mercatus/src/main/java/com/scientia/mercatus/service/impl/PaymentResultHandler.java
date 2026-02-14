package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.entity.Order;
import com.scientia.mercatus.entity.OrderItem;
import com.scientia.mercatus.entity.OrderPaymentStatus;
import com.scientia.mercatus.entity.OrderStatus;
import com.scientia.mercatus.repository.OrderRepository;
import com.scientia.mercatus.service.IInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentResultHandler {

    private final OrderRepository orderRepository;
    private final IInventoryService inventoryService;

    /* Moved  */
    @Transactional
    public void onPaymentSuccess(String orderReference) {

        Order order = orderRepository
                .findByOrderReferenceForUpdate(orderReference)
                .orElseThrow();

        if (order.getOrderPaymentStatus() == OrderPaymentStatus.SUCCESS) {
            return;
        }

        if (order.getStatus() != OrderStatus.PAYMENT_PENDING) {
            return;
        }

        order.setOrderPaymentStatus(OrderPaymentStatus.SUCCESS);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void finalizePaidOrder(String orderReference) {

        Order order = orderRepository
                .findByOrderReferenceForUpdate(orderReference)
                .orElseThrow();

        if (order.getStatus() != OrderStatus.PAYMENT_PENDING) {
            return;
        }

        for (OrderItem item : order.getOrderItems()) {
            inventoryService.confirmReservation(item.getReservationKey());
        }

        order.setStatus(OrderStatus.CONFIRMED);
    }

    @Transactional
    public void onPaymentFailure(String orderReference) {

        Order order = orderRepository
                .findByOrderReferenceForUpdate(orderReference)
                .orElseThrow();

        if (order.getOrderPaymentStatus() == OrderPaymentStatus.SUCCESS) {
            return;
        }

        order.setOrderPaymentStatus(OrderPaymentStatus.FAILED);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void cancelFailedOrder(String orderReference) {

        Order order = orderRepository
                .findByOrderReferenceForUpdate(orderReference)
                .orElseThrow();

        if (order.getStatus() != OrderStatus.PAYMENT_PENDING) {
            return;
        }

        for (OrderItem item : order.getOrderItems()) {
            inventoryService.releaseReservation(item.getReservationKey());
        }

        order.setStatus(OrderStatus.CANCELLED);
    }
}

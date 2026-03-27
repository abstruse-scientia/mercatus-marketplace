package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.entity.Order;
import com.scientia.mercatus.entity.OrderItem;
import com.scientia.mercatus.entity.OrderPaymentStatus;
import com.scientia.mercatus.entity.OrderStatus;
import com.scientia.mercatus.exception.BusinessException;
import com.scientia.mercatus.exception.ErrorEnum;
import com.scientia.mercatus.repository.OrderRepository;
import com.scientia.mercatus.service.IInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentResultHandler {

    private final OrderRepository orderRepository;
    private final IInventoryService inventoryService;

    /* Moved  */
    @Transactional
    public void handlePaymentSuccess(String orderReference) {

        Order order = findOrderForUpdate(orderReference);

        // idempotency
        if (order.getOrderPaymentStatus() == OrderPaymentStatus.SUCCESS &&
                order.getStatus() == OrderStatus.CONFIRMED) {
            return;
        }

        //exception guard
        if (order.getOrderPaymentStatus() != OrderPaymentStatus.PENDING ||
                order.getStatus() != OrderStatus.CREATED) {
            throw new BusinessException(ErrorEnum.INVALID_REQUEST);
        }

        //Confirm inventory deduction
        for (OrderItem orderItem : order.getOrderItems()) {
            inventoryService.confirmReservation(orderItem.getReservationKey());
        }

        order.setStatus(OrderStatus.CONFIRMED);
        order.setOrderPaymentStatus(OrderPaymentStatus.SUCCESS);
    }

    @Transactional
    public void handlePaymentFailure(String orderReference) {

        Order order = findOrderForUpdate(orderReference);

        //idempotency
        if (order.getOrderPaymentStatus() == OrderPaymentStatus.FAILED &&
                order.getStatus() == OrderStatus.CANCELLED) {
            return;
        }


        //critical guard - user already cancelled
        if (order.getStatus() == OrderStatus.CANCELLED &&
                order.getOrderPaymentStatus() ==  OrderPaymentStatus.PENDING) {
            return;
        }

        for (OrderItem orderItem : order.getOrderItems()) {
            inventoryService.releaseReservation(orderItem.getReservationKey());
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setOrderPaymentStatus(OrderPaymentStatus.FAILED);
    }


    private Order findOrderForUpdate(String orderReference) {
        return orderRepository
                .findByOrderReferenceForUpdate(orderReference)
                .orElseThrow(() -> new BusinessException(ErrorEnum.ORDER_NOT_FOUND, "No order found for the order reference provide."));
    }
}

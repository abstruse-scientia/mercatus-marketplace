package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.dto.Order.OrderSummaryDto;
import com.scientia.mercatus.dto.Order.PlaceOrderRequestDto;
import com.scientia.mercatus.dto.Payment.PaymentInitiationResultDto;
import com.scientia.mercatus.entity.*;

import com.scientia.mercatus.exception.*;

import com.scientia.mercatus.repository.OrderRepository;


import com.scientia.mercatus.service.*;
import lombok.RequiredArgsConstructor;



import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

import java.util.*;


@Service
@RequiredArgsConstructor

public class OrderServiceImpl implements IOrderService {


    private final IUserService userService;
    private final OrderRepository orderRepository;
    private final IInventoryService inventoryService;
    private final IPaymentService paymentService;






    @Transactional
    @Override
    public void cancelOrder(Long orderId, Long userId) {
            Order currentOrder = loadAndValidateOrderHelper(orderId, userId);
            if (currentOrder.getStatus().equals(OrderStatus.CANCELLED)) {
                return;
            }
            if (currentOrder.getStatus() != OrderStatus.CREATED) {
                throw new BusinessException(ErrorEnum.INVALID_REQUEST);
            }
            for (OrderItem item: currentOrder.getOrderItems()) {
                inventoryService.releaseReservation(item.getReservationKey());
            }
            currentOrder.setStatus(OrderStatus.CANCELLED);
            currentOrder.setOrderPaymentStatus(OrderPaymentStatus.CANCELLED);
            
    }

    @Override
    public Page<OrderSummaryDto> getOrdersForUser(Long userId, Pageable pageable) {
        if (userId == null) {
            throw new BusinessException(ErrorEnum.NO_LOGGED_IN_USER_FOUND);
        }

        Page<Order> orders = orderRepository.findByUser_UserId(userId, pageable);

        Page<OrderSummaryDto> pageDto = orders.map(order ->
               new OrderSummaryDto(
                       order.getId(),
                       order.getTotalAmount(),
                       order.getOrderPaymentStatus(),
                       order.getStatus(),
                       order.getOrderReference(),
                       order.getCreatedAt()
               )
        );
        return pageDto;


    }


    @Transactional
    @Override
    public PaymentInitiationResultDto initiatePayment(Long orderId, Long userId){
        Order order = loadAndValidateOrderHelper(orderId, userId);
        if(!order.getStatus().equals(OrderStatus.CREATED)) {
            throw new BusinessException(ErrorEnum.INVALID_REQUEST, "Payment can only be initiated in CREATED state");
        }
        if(!order.getOrderPaymentStatus().equals(OrderPaymentStatus.PENDING)) {
            throw new BusinessException(ErrorEnum.INVALID_REQUEST, "Order can not be placed in current payment state");
        }
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        return paymentService.initiatePayment(order.getOrderReference(), "INR", PaymentProvider.RAZORPAY);
    }





    @Override
    public Optional<Order> getExistingOrder(Long userId, String orderReference) {
        //Idempotency key (OrderReference + userId )
        return orderRepository.findByOrderReferenceAndUser_UserId
                (orderReference, userId);
    }

    @Override
    public Order makeOrderSkeleton(Long userId, String orderReference, AddressSnapshot addressSnapshot) {
        Order newOrder = new Order();
        newOrder.setUser(userService.getUser(userId));
        newOrder.setOrderReference(orderReference);
        newOrder.setAddressSnapshot(addressSnapshot);
        newOrder.setTotalAmount(BigDecimal.ZERO);
        return orderRepository.save(newOrder);
    }

    /*----------------------------- Helper Functions ------------------------------------------- */




    private Order loadAndValidateOrderHelper(Long orderId, Long userId) {
        if (orderId == null) {
            throw new BusinessException(ErrorEnum.INVALID_REQUEST, "Order Id can not be null");
        }
        if (userId == null) {
            throw new BusinessException(ErrorEnum.NO_LOGGED_IN_USER_FOUND);
        }
        Order order = orderRepository.findByIdForUpdate(orderId).orElseThrow(
                ()->  new BusinessException(ErrorEnum.ORDER_NOT_FOUND)
        );
        if (!order.getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorEnum.FORBIDDEN_OPERATION);
        }
        return order;
    }
}

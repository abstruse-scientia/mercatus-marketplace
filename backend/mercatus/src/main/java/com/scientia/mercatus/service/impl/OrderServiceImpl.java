package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.dto.Order.OrderItemSummaryDto;
import com.scientia.mercatus.dto.Order.OrderResponseDto;
import com.scientia.mercatus.dto.Order.OrderSummaryDto;
import com.scientia.mercatus.dto.Payment.PaymentInitiationResultDto;
import com.scientia.mercatus.entity.*;

import com.scientia.mercatus.exception.*;

import com.scientia.mercatus.mapper.OrderMapper;
import com.scientia.mercatus.repository.OrderRepository;


import com.scientia.mercatus.service.*;
import lombok.RequiredArgsConstructor;



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
    private final OrderMapper mapper;






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
            
    }

    @Transactional(readOnly = true)
    @Override
    public OrderResponseDto getOrderById(Long orderId, Long userId) {
        if (orderId == null) {
            throw new BusinessException(ErrorEnum.INVALID_REQUEST, "Order Id can not be null");
        }
        if (userId == null) {
            throw new BusinessException(ErrorEnum.NO_LOGGED_IN_USER_FOUND);
        }

        Order currentOrder = orderRepository.findById(orderId).orElseThrow(
                ()->  new BusinessException(ErrorEnum.ORDER_NOT_FOUND)
        );
        if (!currentOrder.getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorEnum.FORBIDDEN_OPERATION);
        }
        
        return mapper.toResponseDto(currentOrder);

    }

    @Override
    public Page<OrderSummaryDto> getOrdersForUser(Long userId, OrderStatus status, Pageable pageable) {
        Page<Order> orders;
        if (userId == null) {
            throw new BusinessException(ErrorEnum.NO_LOGGED_IN_USER_FOUND);
        }

        if (status == null) {
           orders = orderRepository.findByUser_UserId(userId, pageable);
        } else {
            orders = orderRepository.findByUserIdAndStatus(userId, status, pageable);
        }
        Page<OrderSummaryDto> pageDto = orders.map(order -> {

            List<OrderItemSummaryDto> itemSummaries = order.getOrderItems().stream()
                    .map(item -> new OrderItemSummaryDto(
                            item.getProductId(),
                            item.getProductName(),
                            item.getPrimaryImageUrl()
                    )).toList();
            return new OrderSummaryDto(
                    order.getId(),
                    order.getTotalAmount(),
                    order.getOrderPaymentStatus(),
                    order.getStatus(),
                    order.getOrderReference(),
                    order.getCreatedAt(),
                    itemSummaries
            );

        });
        return pageDto;
    }

    @Override
    public Page<OrderSummaryDto> getOrdersForUser(Long userId, Pageable pageable) {
            return getOrdersForUser(userId, null, pageable);
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

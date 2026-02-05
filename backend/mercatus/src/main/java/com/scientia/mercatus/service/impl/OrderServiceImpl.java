package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.dto.Cart.CartContextDto;
import com.scientia.mercatus.dto.Order.OrderSummaryDto;
import com.scientia.mercatus.dto.Payment.PaymentIntentResultDto;
import com.scientia.mercatus.entity.*;

import com.scientia.mercatus.exception.NoLoggedInUserFoundException;
import com.scientia.mercatus.exception.OrderNotFoundException;
import com.scientia.mercatus.exception.UnauthorizedOperationException;
import com.scientia.mercatus.mapper.OrderMapper;
import com.scientia.mercatus.repository.OrderRepository;
import com.scientia.mercatus.repository.PaymentRepository;
import com.scientia.mercatus.repository.UserRepository;
import com.scientia.mercatus.service.ICartService;
import com.scientia.mercatus.service.IInventoryService;
import com.scientia.mercatus.service.IOrderService;
import com.scientia.mercatus.service.IPaymentService;
import lombok.RequiredArgsConstructor;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor

public class OrderServiceImpl implements IOrderService {

    private final ICartService cartService;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final IInventoryService inventoryService;
    private final IPaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final StripePaymentGateway stripePaymentGateway;


    @Override
    @Transactional
    public Order placeOrder(String sessionId, Long userId, String orderReference) {
        try {
            return placeOrderHelper(sessionId, userId, orderReference);
        }catch(DataIntegrityViolationException ex){
            return orderRepository.findByOrderReference(orderReference).orElseThrow();
        }
    }



    @Transactional
    @Override
    public void cancelOrder(Long orderId, Long userId) {
            Order currentOrder = loadAndValidateOrderHelper(orderId, userId);
            if (currentOrder.getStatus().equals(OrderStatus.CANCELLED)) {
                return;
            }
            if (currentOrder.getStatus() != OrderStatus.CREATED) {
                throw new IllegalStateException("Order cannot be cancelled in state"
                        + currentOrder.getStatus());
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
            throw new NoLoggedInUserFoundException("User not found");
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
    public PaymentIntentResultDto initiatePayment(Long orderId, Long userId) {
        Order order = loadAndValidateOrderHelper(orderId, userId);
        if(!order.getStatus().equals(OrderStatus.CREATED)) {
            throw new IllegalStateException("Payment can only be initiated in CREATED state");
        }
        if(!order.getOrderPaymentStatus().equals(OrderPaymentStatus.PENDING)) {
            throw new IllegalStateException("Order can not be placed in current payment state");
        }
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        Long amountMinor = order.getTotalAmount().movePointRight(2).longValueExact();
        return stripePaymentGateway.createPaymentIntent(order.getOrderReference(),
                amountMinor, "INR");
    }





    @Transactional
    @Override
    public void handlePaymentSuccess(String providerPaymentId) {
        paymentService.markPaymentSuccess(providerPaymentId);
        Payment payment = paymentRepository.findByProviderPaymentId(providerPaymentId).orElseThrow();
        Order order = orderRepository.findByOrderReference(payment.getOrderReference()).orElseThrow();
        if (order.getOrderPaymentStatus().equals(OrderPaymentStatus.SUCCESS)) {
            return;
        }
        for(OrderItem item : order.getOrderItems()) { // confirm reservation
            inventoryService.confirmReservation(item.getReservationKey());
        }
        order.setOrderPaymentStatus(OrderPaymentStatus.SUCCESS);
        order.setStatus(OrderStatus.CONFIRMED);
    }


    @Transactional
    @Override
    public void handlePaymentFailure(String providerPaymentId) {
        paymentService.markPaymentFailed(providerPaymentId);
        Payment payment = paymentRepository.findByProviderPaymentId(providerPaymentId).orElseThrow();
        Order order = orderRepository.findByOrderReference(payment.getOrderReference()).orElseThrow();
        if (order.getOrderPaymentStatus().equals(OrderPaymentStatus.FAILED)) {
            return;
        }
        for(OrderItem item : order.getOrderItems()) { // cancel reservation
            inventoryService.releaseReservation(item.getReservationKey());
        }
        order.setOrderPaymentStatus(OrderPaymentStatus.FAILED);
        order.setStatus(OrderStatus.CANCELLED);
    }





    /*----------------------------- Helper Functions ------------------------------------------- */






    Order placeOrderHelper(String sessionId, Long userId, String orderRef) {


        if (orderRef == null) {
            throw new IllegalArgumentException("Order reference is required");
        }

        Optional<Order> existingOrder= orderRepository.findByOrderReference(orderRef);

        if (existingOrder.isPresent()) {
            return existingOrder.get();
        }

        Cart currentCart = cartService.resolveCart(new CartContextDto(sessionId, userId));
        /* Locked cart to ensure concurrency. For example let's say request A has order reference a1
         * and request B has order reference b2 both looking to check out cart with cart_id.
         * Transaction A: resolveCart() -> active , Transaction B: resolveCart -> active.
         * Tx A checks out, then Tx B should not set the cart set to check_out
         * Therefore Tx A: reads cart lock acquired through for update (blocks read for others)
         * Tx B: lock or update fails. Tx A -> checks out -> commit -> lock releases. Tx B -> reads
         * -> sees cart already checked out, therefore fails.*/
        Cart cart = cartService.lockCartForCheckout(currentCart.getCartId());
        if  (cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        Order newOrder = new Order();
        newOrder.setCartId(cart.getCartId());
        newOrder.setUser(userRepository.getReferenceByUserId(userId));
        newOrder.setOrderReference(orderRef);
        Set<CartItem> items = cart.getCartItems();

        Set<OrderItem> orderItems = new LinkedHashSet<>();

        for (CartItem item : items) {
            String reservationKey = UUID.randomUUID().toString();
            Instant expiresAt = Instant.now().plus(10, ChronoUnit.MINUTES);
            Product product = productService.getActiveProduct(item.getProduct().getProductId());
            inventoryService.reserveStock(orderRef,
                    reservationKey,
                    product.getSku(),
                    item.getQuantity(),
                    expiresAt);
            OrderItem orderItem = orderMapper.convertCartItemToOrderItem(item, newOrder);
            orderItem.setReservationKey(reservationKey);
            subtotal = subtotal.add(orderItem.getPriceSnapshot().multiply
                    (BigDecimal.valueOf(orderItem.getQuantity())));
            orderItems.add(orderItem);
        }

        newOrder.setOrderItems(orderItems);
        newOrder.setTotalAmount(subtotal);
        cart.setCartStatus(CartStatus.CHECKED_OUT);

        try {
            return orderRepository.saveAndFlush(newOrder);
        } catch (DataIntegrityViolationException e) {
            return orderRepository.findByOrderReference(orderRef).orElseThrow();
        }
    }



    private Order loadAndValidateOrderHelper(Long orderId, Long userId) {
        if (orderId == null) {
            throw new OrderNotFoundException("Order not found");
        }
        if (userId == null) {
            throw new NoLoggedInUserFoundException("User not found");
        }
        Order order = orderRepository.findByIdForUpdate(orderId).orElseThrow(
                ()-> new OrderNotFoundException("Order not found")
        );
        if (!order.getUser().getUserId().equals(userId)) {
            throw new  UnauthorizedOperationException("Unauthorized operation");
        }
        return order;
    }
}

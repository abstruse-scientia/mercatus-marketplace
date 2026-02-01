package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.dto.Cart.CartContextDto;
import com.scientia.mercatus.dto.Order.OrderSummaryDto;
import com.scientia.mercatus.entity.*;

import com.scientia.mercatus.exception.NoLoggedInUserFoundException;
import com.scientia.mercatus.exception.OrderNotFoundException;
import com.scientia.mercatus.exception.UnauthorizedOperationException;
import com.scientia.mercatus.mapper.OrderMapper;
import com.scientia.mercatus.repository.OrderRepository;
import com.scientia.mercatus.repository.UserRepository;
import com.scientia.mercatus.service.ICartService;
import com.scientia.mercatus.service.IInventoryService;
import com.scientia.mercatus.service.IOrderService;
import com.scientia.mercatus.service.IProductService;
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
    private final IProductService  productService;


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
            if (orderId == null) {
                throw new OrderNotFoundException("Order not found");
            }
            if (userId == null) {
                throw new NoLoggedInUserFoundException("User not found");
            }



            Order currentOrder = orderRepository.findByIdForUpdate(orderId).orElseThrow(
                    ()->  new OrderNotFoundException("Order not found")
            );
            if (!currentOrder.getUser().getUserId().equals(userId)) {
                throw new UnauthorizedOperationException("Given user can not cancel this order");
            }


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
            currentOrder.setPaymentStatus(PaymentStatus.CANCELLED);
            
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
                       order.getPaymentStatus(),
                       order.getStatus(),
                       order.getOrderReference(),
                       order.getCreatedAt()
               )
        );
        return pageDto;


    }



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
}

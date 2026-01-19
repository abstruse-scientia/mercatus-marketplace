package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.dto.CartContextDto;
import com.scientia.mercatus.entity.*;

import com.scientia.mercatus.exception.CartNotFoundException;
import com.scientia.mercatus.exception.NoLoggedInUserFoundException;
import com.scientia.mercatus.mapper.OrderItemMapper;
import com.scientia.mercatus.repository.OrderRepository;
import com.scientia.mercatus.repository.UserRepository;
import com.scientia.mercatus.service.ICartService;
import com.scientia.mercatus.service.IOrderService;
import lombok.RequiredArgsConstructor;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashSet;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements IOrderService {

    private final ICartService cartService;
    private final OrderItemMapper orderItemMapper;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;


    @Override
    public Order placeOrder(String sessionId, Long userId, String orderReference) {


        if (orderReference == null) {
            throw new IllegalArgumentException("Order reference is required");
        }

        Optional<Order> existingOrder= orderRepository.findByOrderReference(orderReference);

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
        newOrder.setOrderReference(orderReference);
        Set<CartItem> items = cart.getCartItems();

        Set<OrderItem> orderItems = new LinkedHashSet<>();

        for (CartItem item : items) {
            OrderItem orderItem = orderItemMapper.convertCartItemToOrderItem(item, newOrder);
            subtotal = subtotal.add(orderItem.getPriceSnapshot().multiply
                    (BigDecimal.valueOf(orderItem.getQuantity())));
            orderItems.add(orderItem);
        }

        newOrder.setOrderItems(orderItems);
        newOrder.setTotalAmount(subtotal);
        cart.setCartStatus(CartStatus.CHECKED_OUT);

        try {
            return orderRepository.save(newOrder);
        } catch (DataIntegrityViolationException e) {
            return orderRepository.findByOrderReference(orderReference).orElseThrow();
        }
    }
}

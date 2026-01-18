package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.dto.CartContextDto;
import com.scientia.mercatus.entity.*;

import com.scientia.mercatus.exception.CartNotFoundException;
import com.scientia.mercatus.exception.NoLoggedInUserFoundException;
import com.scientia.mercatus.mapper.OrderItemMapper;
import com.scientia.mercatus.repository.OrderRepository;
import com.scientia.mercatus.service.ICartService;
import com.scientia.mercatus.service.IOrderService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashSet;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements IOrderService {

    private final ICartService cartService;
    private final OrderItemMapper orderItemMapper;
    private final OrderRepository orderRepository;


    @Override
    public Order placeOrder(String sessionId, Long userId) {
        Cart currentCart = cartService.resolveCart(new CartContextDto(sessionId, userId));

        if  (currentCart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        if (currentCart.getCartStatus() != CartStatus.ACTIVE) {
            throw new IllegalStateException("Cart already checked out.");
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        Order newOrder = new Order();
        newOrder.setCartId(currentCart.getCartId());
        newOrder.setUser(currentCart.getUser());
        Set<CartItem> items = currentCart.getCartItems();

        Set<OrderItem> orderItems = new LinkedHashSet<>();

        for (CartItem item : items) {
            OrderItem orderItem = orderItemMapper.convertCartItemToOrderItem(item, newOrder);
            subtotal = subtotal.add(orderItem.getPriceSnapshot().multiply
                    (BigDecimal.valueOf(orderItem.getQuantity())));
            orderItems.add(orderItem);
        }

        newOrder.setOrderItems(orderItems);
        newOrder.setTotalAmount(subtotal);
        currentCart.setCartStatus(CartStatus.CHECKED_OUT);

        return orderRepository.save(newOrder);
    }
}

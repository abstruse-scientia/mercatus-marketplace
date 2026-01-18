package com.scientia.mercatus.mapper;

import com.scientia.mercatus.entity.CartItem;
import com.scientia.mercatus.entity.Order;
import com.scientia.mercatus.entity.OrderItem;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class OrderItemMapper {

    public OrderItem convertCartItemToOrderItem(CartItem cartItem, Order order) {

        OrderItem orderItem = new OrderItem();
        orderItem.setPriceSnapshot(cartItem.getProduct().getPrice());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setProductId(cartItem.getProduct().getProductId());
        orderItem.setProductName(cartItem.getProduct().getName());
        orderItem.setOrder(order);

        return orderItem;

    }
}

package com.scientia.mercatus.mapper;

import com.scientia.mercatus.dto.Order.OrderItemDto;
import com.scientia.mercatus.dto.Order.OrderResponseDto;
import com.scientia.mercatus.entity.CartItem;
import com.scientia.mercatus.entity.Order;
import com.scientia.mercatus.entity.OrderItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderMapper {

    public OrderItem convertCartItemToOrderItem(CartItem cartItem, Order order) {

        OrderItem orderItem = new OrderItem();
        orderItem.setPriceSnapshot(cartItem.getProduct().getPrice());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setProductId(cartItem.getProduct().getProductId());
        orderItem.setProductName(cartItem.getProduct().getName());
        orderItem.setOrder(order);

        return orderItem;

    }

    public OrderResponseDto toResponseDto(Order order) {
        List<OrderItemDto> itemDtos = order.getOrderItems().stream()
                .map(item -> new OrderItemDto(
                        item.getProductId(),
                    item.getQuantity(),
                    item.getPriceSnapshot(),
                    item.getProductName()
                )).toList();
        return new OrderResponseDto(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                itemDtos
        );
    }
}

package com.scientia.mercatus.factory;

import com.scientia.mercatus.entity.Order;
import com.scientia.mercatus.entity.OrderItem;
import com.scientia.mercatus.entity.Product;

import java.util.UUID;

public class OrderItemFactory {

    public static OrderItem create(Product product, Order order, int quantity) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProductId(product.getProductId());
        orderItem.setProductName(product.getName());
        orderItem.setQuantity(quantity);
        orderItem.setPriceSnapshot(product.getPrice());
        orderItem.setReservationKey("reservationKey-" + UUID.randomUUID());
        return orderItem;
    }

}

package com.scientia.mercatus.factory;

import com.scientia.mercatus.entity.AddressSnapshot;
import com.scientia.mercatus.entity.Order;
import com.scientia.mercatus.entity.User;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderFactory {

    public static Order create(User user, AddressSnapshot snapshot) {
        Order  order = new Order();
        order.setTotalAmount(BigDecimal.valueOf(1000));
        order.setOrderReference("orderRef-" + UUID.randomUUID());
        order.setUser(user);
        order.setAddressSnapshot(snapshot);
        return order;
    }
}

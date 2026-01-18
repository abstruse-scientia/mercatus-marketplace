package com.scientia.mercatus.service;

import com.scientia.mercatus.dto.CartContextDto;
import com.scientia.mercatus.entity.Order;

public interface IOrderService {
    public Order placeOrder(String sessionId, Long userId);
}

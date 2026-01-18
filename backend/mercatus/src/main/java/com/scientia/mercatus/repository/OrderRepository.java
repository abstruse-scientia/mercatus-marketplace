package com.scientia.mercatus.repository;

import com.scientia.mercatus.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}

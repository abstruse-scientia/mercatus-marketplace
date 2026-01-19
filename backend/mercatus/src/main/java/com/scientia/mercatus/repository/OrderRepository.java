package com.scientia.mercatus.repository;

import com.scientia.mercatus.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    Optional<Order> findByOrderReference(String orderReference);
}

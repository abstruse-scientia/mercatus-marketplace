package com.scientia.mercatus.repository;

import com.scientia.mercatus.entity.Order;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderReference(String orderReference);



    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select o from Order o where o.id = :orderId
     """)
    Optional<Order> findByIdForUpdate(@Param("orderId") Long orderId);

    Page<Order> findByUser_UserId(Long userId, Pageable pageable);
}

package com.scientia.mercatus.repository;

import com.scientia.mercatus.entity.Cart;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser_UserId(Long userId);
    Optional<Cart> findBySessionId(String sessionId);

    void deleteBySessionId(String sessionId);

    Optional<Cart> findByCartId(Long cartId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select c from Cart c
            where c.cartId = :cartId
                        and c.cartStatus = 'ACTIVE'
            """)
    Optional<Cart> findActiveCartForUpdate(@Param("cartId")Long cartId);


}

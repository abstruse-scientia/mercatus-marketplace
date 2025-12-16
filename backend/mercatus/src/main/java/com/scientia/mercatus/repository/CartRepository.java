package com.scientia.mercatus.repository;

import com.scientia.mercatus.entity.Cart;
import com.scientia.mercatus.entity.User;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser_UserId(Long userId);
    Optional<Cart> findBySessionId(String sessionId);

    void deleteBySessionId(String sessionId);

    Optional<Cart> findByCartId(Long cartId);
}

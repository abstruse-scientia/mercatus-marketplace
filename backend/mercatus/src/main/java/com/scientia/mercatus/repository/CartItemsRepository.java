package com.scientia.mercatus.repository;

import com.scientia.mercatus.entity.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemsRepository extends JpaRepository<CartItems, Long> {
    Optional<CartItems> findByCart_CartIdAndProduct_ProductId(Long cartId, Long productId);
}

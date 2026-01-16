package com.scientia.mercatus.repository;

import com.scientia.mercatus.entity.Cart;
import com.scientia.mercatus.entity.CartItems;
import com.scientia.mercatus.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemsRepository extends JpaRepository<CartItems, Long> {
    Optional<CartItems> findByCartAndProduct(Cart cart, Product product);

    @Query("""
            select ci from CartItems ci join fetch ci.product where ci.cart = :cart
            """)
    List<CartItems> findByCartWithProduct(@Param("cart") Cart cart);

    void deleteByCart(Cart currentCart);

    Cart cart(Cart cart);
}

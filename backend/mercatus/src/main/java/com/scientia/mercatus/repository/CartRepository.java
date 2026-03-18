package com.scientia.mercatus.repository;

import com.scientia.mercatus.entity.Cart;
import com.scientia.mercatus.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select c from Cart c where c.user.userId = :userId
    """)
    Optional<Cart> findByUser_UserIdForUpdate(@Param("userId")Long userId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select c from Cart c where c.sessionId = :sessionId
    """)
    Optional<Cart> findBySessionIdForUpdate(@Param("sessionId") String sessionId);



    Optional<Cart> findBySessionId(String sessionId);

    void deleteBySessionId(String sessionId);

    Optional<Cart> findByCartId(Long cartId);

    Cart findByUser(User user);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select c from Cart c
            where c.cartId = :cartId
                        and c.cartStatus = 'ACTIVE'
            """)
    Optional<Cart> findActiveCartForUpdate(@Param("cartId")Long cartId);

    @Query("""
        select c from Cart c
        left join fetch c.cartItems
        where c.user.userId = :userId
    """)
    Optional<Cart> findCartWithItems(@Param("userId") Long userId);
}

package com.scientia.mercatus.repository;


import com.scientia.mercatus.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {


    Optional<UserAddress> findByIdAndUserIdAndIsActiveTrue(Long addressId,  Long userId);
    List<UserAddress> findByUserIdAndIsActiveTrue(Long userId);

    Optional<UserAddress> findByUserIdAndIsDefaultTrueAndIsActiveTrue(Long userId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserAddress U " +
            "SET U.isDefault = false " +
            "WHERE U.isActive = true AND " +
            "U.isDefault = true AND " +
            "U.userId = :userId")
    void clearDefaultUser(@Param("userId") Long userId);
}

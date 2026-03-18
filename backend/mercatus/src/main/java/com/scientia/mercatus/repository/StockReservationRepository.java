package com.scientia.mercatus.repository;


import com.scientia.mercatus.entity.ReservationStatus;
import com.scientia.mercatus.entity.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {
    StockReservation findByReservationKey(String reservationKey);

    @Query("select sc from StockReservation sc where sc.orderReference = :orderReference")
    Optional<StockReservation> findByOrderReference(@Param("orderReference") String orderReference);

    List<StockReservation> findByStatusAndExpiresAtBefore(ReservationStatus reservationStatus, Instant now);
}

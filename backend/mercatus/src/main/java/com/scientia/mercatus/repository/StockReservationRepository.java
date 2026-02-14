package com.scientia.mercatus.repository;


import com.scientia.mercatus.entity.ReservationStatus;
import com.scientia.mercatus.entity.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {
    StockReservation findByReservationKey(String reservationKey);

    List<StockReservation> findByStatusAndExpiresAtBefore(ReservationStatus reservationStatus, Instant now);
}

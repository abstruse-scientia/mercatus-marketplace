package com.scientia.mercatus.repository;


import com.scientia.mercatus.entity.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {
    StockReservation findByReservationKey(String reservationKey);
}

package com.scientia.mercatus.service;

import com.scientia.mercatus.entity.StockReservation;

import java.time.Instant;

public interface IInventoryService {

    int getAvailableStock(String sku);

    StockReservation reserveStock(String orderReference,
                                  String reservationKey, String sku, int quantity, Instant expiresAt);

    void confirmReservation(String reservationKey);

    void releaseReservation(String reservationKey);

    void addStock(String sku, int quantity);


}

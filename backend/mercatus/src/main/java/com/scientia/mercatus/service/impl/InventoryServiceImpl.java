package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.entity.InventoryItem;
import com.scientia.mercatus.entity.ReservationStatus;
import com.scientia.mercatus.entity.StockReservation;

import com.scientia.mercatus.exception.InsufficientStockException;
import com.scientia.mercatus.exception.InvalidQuantityException;
import com.scientia.mercatus.exception.ReservationAlreadyExpiredException;
import com.scientia.mercatus.exception.InventoryItemNotFoundException;
import com.scientia.mercatus.repository.InventoryItemRepository;
import com.scientia.mercatus.repository.StockReservationRepository;
import com.scientia.mercatus.service.IInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements IInventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final StockReservationRepository stockReservationRepository;

    @Override
    @Transactional
    public StockReservation reserveStock(String orderReference, String reservationKey, String sku, int quantity, Instant expiresAt) {
        StockReservation stockReservation = stockReservationRepository.findByReservationKey(reservationKey);
        if (stockReservation != null) {
            return stockReservation;
        }
        if (quantity <= 0) {
            throw new InvalidQuantityException("Invalid quantity");
        }
        if (expiresAt.isBefore(Instant.now())) {
            throw new ReservationAlreadyExpiredException("Reservation already expired");
        }
        InventoryItem inventoryItem = inventoryItemRepository.findBySku(sku)
                .orElseThrow(() -> new InventoryItemNotFoundException("No stock found."));
        int available = inventoryItem.getTotalStock() - inventoryItem.getReservedStock();
        if (available < quantity) {
            throw new InsufficientStockException("Insufficient stock.");
        }
        StockReservation reservation = new StockReservation();
        reservation.setReservationKey(UUID.randomUUID().toString());
        reservation.setSku(sku);
        reservation.setQuantity(quantity);
        reservation.setExpiresAt(expiresAt);
        reservation.setStatus(ReservationStatus.RESERVED);
        return reservation;
    }



    @Override
    public void addStock(String sku, int quantity) {

    }

    @Override
    public void confirmReservation(String reservationKey) {

    }

    @Override
    public int getAvailableStock(String sku) {
        return 0;
    }

    @Override
    public void releaseReservation(String reservationKey) {

    }


}

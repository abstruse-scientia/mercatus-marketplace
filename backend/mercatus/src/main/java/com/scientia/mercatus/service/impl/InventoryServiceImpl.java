package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.entity.InventoryItem;
import com.scientia.mercatus.entity.ReservationStatus;
import com.scientia.mercatus.entity.StockReservation;

import com.scientia.mercatus.exception.*;
import com.scientia.mercatus.repository.InventoryItemRepository;
import com.scientia.mercatus.repository.StockReservationRepository;
import com.scientia.mercatus.service.IInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;


@Slf4j
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
            log.warn("Insufficient stock for SKU: {}", sku);
            throw new InsufficientStockException("Insufficient stock for requested items");
        }

        inventoryItem.setReservedStock(inventoryItem.getReservedStock() + quantity) ;
        StockReservation reservation = new StockReservation();
        reservation.setReservationKey(reservationKey);
        reservation.setOrderReference(orderReference);
        reservation.setSku(sku);
        reservation.setQuantity(quantity);
        reservation.setExpiresAt(expiresAt);
        reservation.setStatus(ReservationStatus.RESERVED);

        inventoryItemRepository.save(inventoryItem);
        stockReservationRepository.save(reservation);
        return reservation;
    }



    @Override
    @Transactional
    public void addStock(String sku, int quantity) {
        InventoryItem item = inventoryItemRepository.findBySku(sku).orElseGet(
                    ()-> {
                        InventoryItem inventoryItem = new InventoryItem();
                        inventoryItem.setSku(sku);
                        inventoryItem.setTotalStock(0);
                        inventoryItem.setReservedStock(0);
                        return inventoryItem;
                    }
                );
        if (quantity <= 0) {
            throw new InvalidQuantityException("Invalid quantity");
        }
        item.setTotalStock(item.getTotalStock() + quantity);
        inventoryItemRepository.save(item);
    }

    @Override
    @Transactional
    public void confirmReservation(String reservationKey) {
        StockReservation stockReservation = stockReservationRepository.findByReservationKey(reservationKey);
        if (stockReservation == null) {
            throw new ReservationNotExistsException("No reservation exists for this reservation key." + reservationKey);
        }
        if (!stockReservation.getStatus().equals(ReservationStatus.RESERVED)) {
            throw new InvalidReservationStateException("Invalid reservation state");
        }
        InventoryItem item = inventoryItemRepository.findBySku(stockReservation.getSku())
                .orElseThrow(() -> new InventoryItemNotFoundException("No stock found."));
        int quantity = stockReservation.getQuantity();
        item.setReservedStock(item.getReservedStock() - quantity);
        item.setTotalStock(item.getTotalStock() - quantity);
        stockReservation.setStatus(ReservationStatus.CONFIRMED);
        stockReservationRepository.save(stockReservation);
        inventoryItemRepository.save(item);
    }

    @Override
    public int getAvailableStock(String sku) {
        InventoryItem item = inventoryItemRepository.findBySku(sku).orElseThrow(() ->
                new InventoryItemNotFoundException("No stock found." + sku));
        return item.getTotalStock() - item.getReservedStock();
    }

    @Override
    @Transactional
    public void releaseReservation(String reservationKey) {
        StockReservation stockReservation = stockReservationRepository.findByReservationKey(reservationKey);
        if (stockReservation == null) {
            throw new ReservationNotExistsException("No reservation exists for this reservation key." + reservationKey);
        }
        if (!stockReservation.getStatus().equals(ReservationStatus.RESERVED)) {
            throw new InvalidReservationStateException("Invalid reservation state");
        }
        releaseInternal(stockReservation);
    }


    private void releaseInternal(StockReservation stockReservation) {
        if(stockReservation.getStatus() != (ReservationStatus.RESERVED)) {
            return;
        }

        int quantity = stockReservation.getQuantity();
        InventoryItem inventoryItem = inventoryItemRepository.findBySku(stockReservation.getSku())
                .orElseThrow(() -> new InventoryItemNotFoundException("No stock found."));
        inventoryItem.setReservedStock(inventoryItem.getReservedStock() - quantity);
        stockReservation.setStatus(ReservationStatus.RELEASED);
        inventoryItemRepository.save(inventoryItem);
        stockReservationRepository.save(stockReservation);

    }



    @Transactional
    public void expireReservations() {

        List<StockReservation> expired =
                stockReservationRepository
                        .findByStatusAndExpiresAtBefore(
                                ReservationStatus.RESERVED,
                                Instant.now()
                        );

        for (StockReservation r : expired) {
            releaseInternal(r);
        }
    }


}

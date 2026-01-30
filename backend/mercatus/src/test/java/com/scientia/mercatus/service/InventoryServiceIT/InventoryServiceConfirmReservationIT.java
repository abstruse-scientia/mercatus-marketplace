package com.scientia.mercatus.service.InventoryServiceIT;

import com.scientia.mercatus.entity.InventoryItem;
import com.scientia.mercatus.entity.ReservationStatus;
import com.scientia.mercatus.entity.StockReservation;
import com.scientia.mercatus.exception.ReservationNotExistsException;
import com.scientia.mercatus.repository.InventoryItemRepository;
import com.scientia.mercatus.repository.StockReservationRepository;
import com.scientia.mercatus.service.IInventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
@Transactional
@ActiveProfiles("test")
public class InventoryServiceConfirmReservationIT {


    @Autowired
    private IInventoryService inventoryService;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private StockReservationRepository stockReservationRepository;
    @Test
    void shouldConfirmReservationAndReduceStock() {
        inventoryService.addStock("SKU-1", 10);

        inventoryService.reserveStock(
                "O1",
                "R1",
                "SKU-1",
                3,
                Instant.now().plus(15, ChronoUnit.MINUTES)
        );

        inventoryService.confirmReservation("R1");

        InventoryItem item =
                inventoryItemRepository.findBySku("SKU-1").orElseThrow();
        StockReservation reservation =
                stockReservationRepository.findByReservationKey("R1");

        assertEquals(7, item.getTotalStock());
        assertEquals(0, item.getReservedStock());
        assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());
    }

    @Test
    void shouldFailIfReservationNotInReservedState() {
        assertThrows(
                ReservationNotExistsException.class,
                () -> inventoryService.confirmReservation("Dummy")
        );
    }
}

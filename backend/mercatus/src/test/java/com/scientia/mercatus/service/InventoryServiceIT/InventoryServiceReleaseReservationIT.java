package com.scientia.mercatus.service.InventoryServiceIT;


import com.scientia.mercatus.entity.InventoryItem;
import com.scientia.mercatus.entity.ReservationStatus;
import com.scientia.mercatus.entity.StockReservation;
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

@SpringBootTest
@Testcontainers
@Transactional
@ActiveProfiles("test")
public class InventoryServiceReleaseReservationIT {



    @Autowired
    private IInventoryService inventoryService;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private StockReservationRepository stockReservationRepository;


    @Test
    void shouldReleaseReservationAndRestoreStock() {
        inventoryService.addStock("SKU-1", 10);

        inventoryService.reserveStock(
                "O1","R1","SKU-1",4, Instant.now().plus(15, ChronoUnit.MINUTES)
        );

        inventoryService.releaseReservation("R1");

        InventoryItem item =
                inventoryItemRepository.findBySku("SKU-1").orElseThrow();
        StockReservation reservation =
                stockReservationRepository.findByReservationKey("R1");

        assertEquals(0, item.getReservedStock());
        assertEquals(ReservationStatus.RELEASED, reservation.getStatus());
    }
}

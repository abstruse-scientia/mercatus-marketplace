package com.scientia.mercatus.service.InventoryServiceIT;

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
public class InventoryServiceGetAvailableStockIT {


    @Autowired
    private IInventoryService inventoryService;
    @Test
    void shouldReturnAvailableStockCorrectly() {
        inventoryService.addStock("SKU-1", 10);
        inventoryService.reserveStock(
                "O1","R1","SKU-1",4, Instant.now().plus(15, ChronoUnit.MINUTES)
        );

        assertEquals(6, inventoryService.getAvailableStock("SKU-1"));
    }
}

package com.scientia.mercatus.service.InventoryServiceIT;

import com.scientia.mercatus.entity.InventoryItem;
import com.scientia.mercatus.exception.InvalidQuantityException;
import com.scientia.mercatus.repository.InventoryItemRepository;
import com.scientia.mercatus.service.IInventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Testcontainers
public class InventoryServiceAddStockIT {

    @Autowired
    private IInventoryService inventoryService;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Test
    void shouldCreateInventoryIfNotExists() {
        inventoryService.addStock("SKU-1", 5);

        InventoryItem item =
                inventoryItemRepository.findBySku("SKU-1").orElseThrow();

        assertEquals(5, item.getTotalStock());
        assertEquals(0, item.getReservedStock());
    }

    @Test
    void shouldIncreaseStockIfExists() {
        inventoryService.addStock("SKU-1", 5);
        inventoryService.addStock("SKU-1", 3);

        InventoryItem item =
                inventoryItemRepository.findBySku("SKU-1").orElseThrow();

        assertEquals(8, item.getTotalStock());
    }

    @Test
    void shouldFailOnInvalidQuantity() {
        assertThrows(
                InvalidQuantityException.class,
                () -> inventoryService.addStock("SKU-1", 0)
        );
    }
}

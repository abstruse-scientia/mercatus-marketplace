package com.scientia.mercatus.service.InventoryServiceIT;



import com.scientia.mercatus.entity.InventoryItem;
import com.scientia.mercatus.entity.StockReservation;
import com.scientia.mercatus.exception.InsufficientStockException;
import com.scientia.mercatus.repository.InventoryItemRepository;
import com.scientia.mercatus.repository.StockReservationRepository;
import com.scientia.mercatus.service.IInventoryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public class InventoryServiceReserveStockIT {

    @Autowired
    private IInventoryService inventoryService;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private StockReservationRepository stockReservationRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldReserveStockSuccessfully() {

        //Arrange
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setSku("SKU1");
        inventoryItem.setTotalStock(10);
        inventoryItemRepository.save(inventoryItem);

        //Act
        StockReservation reservation = inventoryService.reserveStock(
                "OrderRef - 2",
                "OrderItemRef - 2",
                "SKU1",
                2,
                Instant.now().plus(15, ChronoUnit.MINUTES));

        //Assert
        InventoryItem updatedItem = inventoryItemRepository.findBySku("SKU1").orElseThrow();

        assertEquals(2, updatedItem.getReservedStock());
    }


    @Test
    void shouldRollbackWhenInsufficientStock() {
        //Arrange
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setSku("SKU-2");
        inventoryItem.setTotalStock(2);
        inventoryItemRepository.save(inventoryItem);



        //Act
        assertThrows(InsufficientStockException.class, () -> inventoryService.reserveStock(
                "OrderRef-3",
                "OrderItemRef-3",
                "SKU-2",
                5,
                Instant.now().plus(15, ChronoUnit.MINUTES)

        ));

        // then
        InventoryItem unchanged =
                inventoryItemRepository.findBySku("SKU-2").orElseThrow();

        assertEquals(2, unchanged.getTotalStock());
        assertEquals(0, unchanged.getReservedStock());

        assertNull(stockReservationRepository.findByReservationKey("OrderRef-3"));

    }


    @Test
    //Optimistic Locking can only be tested on repository level
    void shouldFailWithOptimisticLockingFailureWhenConcurrentUpdateOccurs() {

        //Arrange
        InventoryItem item3 = new InventoryItem();
        item3.setSku("SKU-4");
        item3.setTotalStock(5);
        inventoryItemRepository.save(item3);


        //
        InventoryItem item1 = inventoryItemRepository.findBySku("SKU-4").orElseThrow();

        entityManager.clear();

        InventoryItem item2 = inventoryItemRepository.findBySku("SKU-4").orElseThrow();

        item1.setTotalStock(10);
        inventoryItemRepository.saveAndFlush(item1);

        item2.setTotalStock(10);

        assertThrows(OptimisticLockingFailureException.class,
                ()-> inventoryItemRepository.saveAndFlush(item2));



    }

    @Test
    void shouldReturnSameReservation_whenCalledTwice() {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setSku("SKU-FUJI");
        inventoryItem.setTotalStock(5);
        inventoryItemRepository.save(inventoryItem);

        StockReservation reservation1 = inventoryService.reserveStock(
                "Order-FUJI",
                "OrderItem-FUJI",
                "SKU-FUJI",
                2,
                Instant.now().plus(15, ChronoUnit.MINUTES));



        StockReservation reservation2 = inventoryService.reserveStock(
                "Order-FUJI",
                "OrderItem-FUJI",
                "SKU-FUJI",
                2,
                Instant.now().plus(15, ChronoUnit.MINUTES));

        InventoryItem item = inventoryItemRepository.findBySku("SKU-FUJI").orElseThrow();
        assertEquals(2, item.getReservedStock());
        assertEquals(reservation1.getId(), reservation2.getId());
    }





}

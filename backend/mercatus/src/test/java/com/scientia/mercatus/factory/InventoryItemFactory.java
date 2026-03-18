package com.scientia.mercatus.factory;

import com.scientia.mercatus.entity.InventoryItem;

import java.util.UUID;

public class InventoryItemFactory {

    public static InventoryItem create() {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setTotalStock(10);
        inventoryItem.setSku("SKU-" + UUID.randomUUID());
        return inventoryItem;
    }
}

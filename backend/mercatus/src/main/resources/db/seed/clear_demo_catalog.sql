
-- Wipes all catalog data (categories, products, images, inventory).
-- Deletion order in accordance to foreign key constraints.
-- 1. Stock reservations (references inventory SKU)
DELETE FROM stock_reservation;

-- 2. Inventory items (linked to products by SKU string, no FK)
DELETE FROM inventory_item;

-- 3. Product images (FK: product on delete cascade, but explicit is clearer)
DELETE FROM product_image;

-- 4. Cart items (FK : product)
DELETE FROM cart_item;

-- 5. Products (FK : category)
DELETE FROM product;

-- 6. Categories (no dependencies after products are gone)
DELETE FROM category;

-- Reset auto increment counters for clean re-seed
ALTER TABLE category AUTO_INCREMENT = 1;
ALTER TABLE product AUTO_INCREMENT = 1;
ALTER TABLE product_image AUTO_INCREMENT = 1;
ALTER TABLE inventory_item AUTO_INCREMENT = 1;

SELECT 'Demo catalog cleared. Restart the app with dev profile to re seed.' AS status;

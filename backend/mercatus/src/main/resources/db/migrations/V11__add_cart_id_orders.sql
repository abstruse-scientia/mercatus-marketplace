ALTER TABLE orders
ADD COLUMN cart_id BIGINT unsigned NULL,
ADD CONSTRAINT fk_orders_cart FOREIGN KEY (cart_id) references cart(cart_id);

CREATE INDEX idx_cart_user ON cart(user_id);
CREATE INDEX idx_items_cart ON cart_items(cart_id);
CREATE INDEX idx_items_product ON cart_items(product_id);
CREATE INDEX idx_inventory_product ON inventory(product_id);
CREATE INDEX idx_orders_user ON orders(user_id);
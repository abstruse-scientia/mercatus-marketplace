DROP TABLE IF EXISTS order_items;
DROP TABLE  IF EXISTS orders;
DROP TABLE  IF EXISTS products;



CREATE table product (
    product_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(12, 2) NOT NULL,
    popularity BIGINT NOT NULL DEFAULT 0,
    total_sold_quantity BIGINT NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    created_by BIGINT UNSIGNED,
    updated_at DATETIME(6),
    updated_by BIGINT UNSIGNED

) engine=InnoDB;


CREATE TABLE orders(
    order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,

    total_amount DECIMAL(12, 2)  NOT NULL,

    user_id BIGINT UNSIGNED NOT NULL,

    payment_status VARCHAR(50)     NOT NULL,
    status VARCHAR(100)    NOT NULL,

    order_reference VARCHAR(255)    NOT NULL,

    created_at DATETIME(6)     NOT NULL,
    created_by BIGINT UNSIGNED,
    updated_at DATETIME(6),
    updated_by BIGINT UNSIGNED,

    CONSTRAINT unique_orders_order_reference
        UNIQUE (order_reference),

    CONSTRAINT foreign_key_orders_user
        FOREIGN KEY (user_id)
            REFERENCES users(user_id)
)engine=InnoDB;


CREATE TABLE order_item (
    order_item_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

    order_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,

    quantity INT UNSIGNED NOT NULL,
    price_snapshot DECIMAL(12, 2) NOT NULL,

    created_at DATETIME(6) NOT NULL,
    created_by BIGINT UNSIGNED,
    updated_at DATETIME(6),
    updated_by BIGINT UNSIGNED,
    PRIMARY KEY (order_item_id),

    CONSTRAINT foreign_key_order_item_order
        FOREIGN KEY (order_id)
        REFERENCES orders(order_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_order_item_product
        FOREIGN KEY (product_id)
        REFERENCES product(product_id)
)engine=InnoDB;







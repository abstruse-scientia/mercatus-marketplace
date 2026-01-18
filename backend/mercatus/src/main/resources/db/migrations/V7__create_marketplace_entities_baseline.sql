


create table category (
                          category_id bigint unsigned not null auto_increment,
                          created_at datetime(6) not null,
                          created_by bigint unsigned,
                          updated_at datetime(6),
                          updated_by bigint unsigned,
                          category_name varchar(100) not null,
                          primary key (category_id)
) engine=InnoDB;


create table product (
                         product_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         description TEXT,
                         price DECIMAL(12, 2) NOT NULL,
                         category_id bigint unsigned not null,
                         popularity BIGINT NOT NULL DEFAULT 0,
                         total_sold_quantity BIGINT NOT NULL DEFAULT 0,
                         created_at DATETIME(6) NOT NULL,
                         created_by BIGINT UNSIGNED,
                         updated_at DATETIME(6),
                         updated_by BIGINT UNSIGNED,
                         constraint fk_product_category_id foreign key (category_id) references category(category_id)
) engine=InnoDB;


create table cart (
                      cart_id bigint unsigned not null auto_increment,
                      created_at datetime(6) not null,
                      created_by bigint unsigned,
                      session_id varchar(100) not null,
                      status ENUM('ACTIVE','CHECKED_OUT','ABANDONED') NOT NULL DEFAULT 'ACTIVE',
                      updated_at datetime(6),
                      updated_by bigint unsigned,
                      user_id bigint unsigned not null,
                      primary key (cart_id),
                      constraint unique_cart_user_id unique(user_id),
                      constraint fk_cart_user_id foreign key (user_id) references users (user_id)

) engine=InnoDB;

create table cart_item (
                            quantity int unsigned not null,
                            cart_id bigint unsigned NOT NULL ,
                            cart_item_id bigint unsigned not null auto_increment,
                            created_at datetime(6) not null,
                            created_by bigint unsigned,
                            product_id bigint unsigned not null,
                            updated_at datetime(6),
                            updated_by bigint unsigned,
                            primary key (cart_item_id),
                            constraint unique_cart_product unique(cart_id, product_id),
                            constraint fk_cart_item_cart_id foreign key (cart_id) references cart (cart_id) on delete cascade ,
                            constraint fk_cart_item_product_id foreign key (product_id) references product (product_id)
) engine=InnoDB;



create table inventory (
                           stock_quantity integer unsigned not null,
                           created_at datetime(6) not null,
                           created_by bigint unsigned,
                           inventory_id bigint unsigned not null auto_increment,
                           product_id bigint unsigned not null,
                           updated_at datetime(6),
                           updated_by bigint unsigned,
                           primary key (inventory_id),
                           constraint unique_inventory_product_id unique(product_id),
                           constraint fk_inventory_product_id foreign key (product_id) references product(product_id)
) engine=InnoDB;

create table orders (
                        order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,

                        total_amount DECIMAL(12, 2)  NOT NULL,

                        user_id BIGINT UNSIGNED NOT NULL,

                        cart_id bigint unsigned NULl,

                        payment_status VARCHAR(50)     NOT NULL,
                        status VARCHAR(100)    NOT NULL,

                        order_reference VARCHAR(255)    NOT NULL,

                        created_at DATETIME(6)     NOT NULL,
                        created_by BIGINT UNSIGNED,
                        updated_at DATETIME(6),
                        updated_by BIGINT UNSIGNED,
                        constraint unique_order_reference unique(order_reference),
                        constraint fk_order_user_id foreign key (user_id) references users(user_id)
) engine=InnoDB;

create table order_item (
                             order_item_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

                             order_id BIGINT UNSIGNED NOT NULL,
                             product_id BIGINT UNSIGNED NOT NULL,
                             product_name varchar(255) not null,

                             quantity INT UNSIGNED NOT NULL,
                             price_snapshot DECIMAL(12, 2) NOT NULL,

                             created_at DATETIME(6) NOT NULL,
                             created_by BIGINT UNSIGNED,
                             updated_at DATETIME(6),
                             updated_by BIGINT UNSIGNED,
                             PRIMARY KEY (order_item_id),
                             constraint fk_order_item_order_id foreign key (order_id) references orders(order_id) on delete cascade
) engine=InnoDB;


CREATE INDEX idx_cart_user ON cart(user_id);
CREATE INDEX idx_items_cart ON cart_item(cart_id);
CREATE INDEX idx_items_product ON cart_item(product_id);
CREATE INDEX idx_inventory_product ON inventory(product_id);
CREATE INDEX idx_orders_user ON orders(user_id);



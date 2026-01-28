drop table if exists inventory;



update product
set sku = CONCAT('SKU-', product_id)
where sku is null;






# Inventory


create table if not exists inventory_item(
    id bigint unsigned auto_increment not null,
    sku varchar(100) not null unique,
    total_stock int not null,
    reserved_stock int not null,
    primary key (id),
    check ( total_stock >= 0 ),
    check ( reserved_stock >= 0 ),
    check (reserved_stock <= total_stock)
);

create table if not exists stock_reservation(
    id bigint unsigned auto_increment not null,
    reservation_key varchar(100) not null unique,
    quantity int not null,
    sku varchar(100) not null,
    status varchar(20) not null,
    expires_at timestamp not null,
    primary key (id),
    check(quantity > 0)
);
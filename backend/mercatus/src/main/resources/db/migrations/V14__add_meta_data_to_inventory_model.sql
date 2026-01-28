alter table inventory_item
add column created_at DATETIME(6) NOT NULL;

alter table inventory_item
add column     created_by BIGINT UNSIGNED;

alter table inventory_item
add column    updated_at DATETIME(6);

alter table inventory_item
add column   updated_by BIGINT UNSIGNED;


alter table stock_reservation
    add column created_at DATETIME(6) NOT NULL;

alter table stock_reservation
    add column     created_by BIGINT UNSIGNED;

alter table stock_reservation
    add column    updated_at DATETIME(6);

alter table stock_reservation
    add column   updated_by BIGINT UNSIGNED;



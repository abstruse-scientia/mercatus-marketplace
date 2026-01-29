alter table order_item
add column reservation_key varchar(100);

update order_item
set reservation_key = concat('LEGACY_ORDER', order_item_id)
where reservation_key is null;


alter table order_item
modify reservation_key varchar(100)  not null;

alter table order_item
add constraint uk_reservation_key_order_item UNIQUE (reservation_key);

create index idx_reservation_key on order_item(reservation_key);
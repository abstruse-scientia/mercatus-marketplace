alter table inventory_item
add column version bigint not null default 0;

alter table inventory_item
modify reserved_stock int not null default 0;

alter table stock_reservation
modify status varchar(20) not null;
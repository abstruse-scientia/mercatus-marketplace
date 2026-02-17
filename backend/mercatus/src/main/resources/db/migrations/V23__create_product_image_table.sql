create table product_image (
    id bigint auto_increment primary key ,
    product_id bigint unsigned not null,
    url varchar(500) not null ,
    is_primary boolean not null default false,
    sort_order int not null default 0,
    created_at DATETIME(6) NOT NULL,
    created_by BIGINT UNSIGNED,
    updated_at DATETIME(6),
    updated_by BIGINT UNSIGNED,
    constraint fk_product_image foreign key (product_id) references product(product_id) on delete cascade

);

create index idx_product_image on product_image(product_id);
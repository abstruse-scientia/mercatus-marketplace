alter table product
add column slug varchar(200) null;

update product
set slug = concat('product-', product_id) where slug is null or slug = '';

alter table product
    modify slug varchar(200) not null;

alter table product
    add constraint uq_product_slug unique (slug);

alter table product
add column primary_image_url varchar(500) null;

-- Simulating partial indexing in MYSQL

alter table product_image
add column primary_flag bigint unsigned
generated always as (
    CASE WHEN is_primary = 1 THEN product_id else null END
)virtual ;

create unique index ux_one_primary_flag_per_product
on product_image(primary_flag);
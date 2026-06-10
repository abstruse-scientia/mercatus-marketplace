alter table order_item add column primary_image_url varchar(255);

update order_item oi
join product p on oi.product_id = p.product_id
set oi.primary_image_url = p.primary_image_url;
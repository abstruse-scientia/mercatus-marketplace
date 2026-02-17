alter table category
add column slug varchar(200) null;


update category
set slug = concat('categoryId-', category_id) where slug is null or slug = '';


alter table category
    modify slug varchar(200) not null;

alter table category
add constraint uq_category_slug unique (slug);
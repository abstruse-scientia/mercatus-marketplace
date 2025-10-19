
create table cart (
                      cart_id bigint not null auto_increment,
                      created_at datetime(6) not null,
                      created_by bigint,
                      updated_at datetime(6),
                      updated_by bigint,
                      user_id bigint not null,
                      primary key (cart_id)
) engine=InnoDB;

create table cart_items (
                            quantity integer not null,
                            cart_id bigint,
                            cart_item_id bigint not null auto_increment,
                            created_at datetime(6) not null,
                            created_by bigint,
                            product_id bigint not null,
                            updated_at datetime(6),
                            updated_by bigint,
                            primary key (cart_item_id)
) engine=InnoDB;

create table category (
                          category_id bigint not null auto_increment,
                          created_at datetime(6) not null,
                          created_by bigint,
                          updated_at datetime(6),
                          updated_by bigint,
                          category_name varchar(100) not null,
                          primary key (category_id)
) engine=InnoDB;

create table inventory (
                           stock_quantity integer not null,
                           created_at datetime(6) not null,
                           created_by bigint,
                           inventory_id bigint not null auto_increment,
                           product_id bigint not null,
                           updated_at datetime(6),
                           updated_by bigint,
                           primary key (inventory_id)
) engine=InnoDB;

create table orders (
                        total_amount decimal(12,2) not null,
                        created_at datetime(6) not null,
                        created_by bigint,
                        order_id bigint not null auto_increment,
                        updated_at datetime(6),
                        updated_by bigint,
                        user_id bigint not null,
                        payment_status varchar(50) not null,
                        status varchar(100) not null,
                        order_reference varchar(255) not null,
                        primary key (order_id)
) engine=InnoDB;

create table order_items (
                             price decimal(38,2),
                             product_popularity integer,
                             subtotal decimal(10,2),
                             created_at datetime(6) not null,
                             created_by bigint,
                             order_id bigint not null,
                             order_item_id bigint not null auto_increment,
                             popularity bigint,
                             product_id bigint not null,
                             quantity bigint,
                             updated_at datetime(6),
                             updated_by bigint,
                             product_name varchar(100) not null,
                             product_description varchar(300) not null,
                             primary key (order_item_id)
) engine=InnoDB;

create table products (
                          popularity integer not null,
                          price decimal(8,2) not null,
                          category_id bigint not null,
                          created_at datetime(6) not null,
                          created_by bigint,
                          product_id bigint not null auto_increment,
                          updated_at datetime(6),
                          updated_by bigint,
                          name varchar(100) not null,
                          description varchar(300) not null,
                          primary key (product_id)
) engine=InnoDB;







alter table cart
    add constraint UK9emlp6m95v5er2bcqkjsw48he unique (user_id);

alter table inventory
    add constraint UKce3rbi3bfstbvvyne34c1dvyv unique (product_id);

alter table orders
    add constraint UK2mnxs4cfnjg2w7q5xw77x91u unique (order_reference);



alter table cart
    add constraint FKmh5bgebkp76p4yr6t1dr1tya4
        foreign key (created_by)
            references users (user_id);

alter table cart
    add constraint FKsyh7kaauej1p9cdxjk98yfn5y
        foreign key (updated_by)
            references users (user_id);

alter table cart
    add constraint FKg5uhi8vpsuy0lgloxk2h4w5o6
        foreign key (user_id)
            references users (user_id);

alter table cart_items
    add constraint FKbhw4r3ymojowg2oitk1q92anh
        foreign key (created_by)
            references users (user_id);

alter table cart_items
    add constraint FKoagcxwgqf1r0cx578r4phswe9
        foreign key (updated_by)
            references users (user_id);

alter table cart_items
    add constraint FK99e0am9jpriwxcm6is7xfedy3
        foreign key (cart_id)
            references cart (cart_id);

alter table cart_items
    add constraint FK1re40cjegsfvw58xrkdp6bac6
        foreign key (product_id)
            references products (product_id);

alter table category
    add constraint FKnbi9umnlfmtbpd3kcs8o37ta3
        foreign key (created_by)
            references users (user_id);

alter table category
    add constraint FKho9xxhac4fwi30iuak1pah7l4
        foreign key (updated_by)
            references users (user_id);

alter table inventory
    add constraint FKkr1hn52ku7b4p1ge591l05t6k
        foreign key (created_by)
            references users (user_id);

alter table inventory
    add constraint FKj9oqhy7kfm0rjoumf7dcc1ywl
        foreign key (updated_by)
            references users (user_id);

alter table inventory
    add constraint FKq2yge7ebtfuvwufr6lwfwqy9l
        foreign key (product_id)
            references products (product_id);

alter table orders
    add constraint FKfkqyksjq8ji7occy87lgns90j
        foreign key (created_by)
            references users (user_id);

alter table orders
    add constraint FKk16sqm3ljf4mfdho333omrjij
        foreign key (updated_by)
            references users (user_id);

alter table orders
    add constraint FKs9p0s8b1nh7m2no87xxteu83x
        foreign key (user_id)
            references users (user_id);

alter table order_items
    add constraint FKs2wcthlv8qp34l56jcwtgj64q
        foreign key (created_by)
            references users (user_id);

alter table order_items
    add constraint FKl145d42twpl0wlnorqvtr7b45
        foreign key (updated_by)
            references users (user_id);

alter table order_items
    add constraint FKdy6l1ieu09exdjope704jwo1e
        foreign key (order_id)
            references orders (order_id);

alter table order_items
    add constraint FKocimc7dtr037rh4ls4l95nlfi
        foreign key (product_id)
            references products (product_id);

alter table products
    add constraint FKl0lce8i162ldn9n01t2a6lcix
        foreign key (created_by)
            references users (user_id);

alter table products
    add constraint FKdeswm6d74skv6do803axl6edj
        foreign key (updated_by)
            references users (user_id);

alter table products
    add constraint FK1cf90etcu98x1e6n9aks3tel3
        foreign key (category_id)
            references category (category_id);


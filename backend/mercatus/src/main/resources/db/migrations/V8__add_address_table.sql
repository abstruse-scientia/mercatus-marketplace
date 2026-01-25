create table address (
    address_id bigint unsigned not null auto_increment,
    user_id bigint unsigned not null,
    full_name varchar(100) not null,
    mobile_number varchar(15) not null,
    flat_house varchar(255) not null,
    area varchar(255) not null,
    landmark varchar(255),
    pincode varchar(10) not null,
    city varchar(50) not null,
    state varchar(50) not null,
    country varchar(50) not null,
    is_default tinyint(1) not null,
    is_active tinyint(1) not null,
    created_at datetime(6) not null,
    created_by bigint unsigned,
    updated_at datetime(6),
    updated_by bigint unsigned,
    primary key (address_id)
);

create index idx_address_id on address(address_id);
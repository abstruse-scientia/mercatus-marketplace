alter table orders
add column full_name varchar(100) not null,
add column    mobile_number varchar(15) not null,
add column    flat_house varchar(255) not null,
add column    area varchar(255) not null,
add column     landmark varchar(255),
add column    pincode varchar(10) not null,
add column   city varchar(50) not null,
add column    state varchar(50) not null,
add column   country varchar(50) not null;
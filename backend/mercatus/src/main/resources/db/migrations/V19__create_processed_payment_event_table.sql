create table processed_payment_event(
    event_id varchar(255) not null primary key,
    processed_at datetime(6) not null default current_timestamp(6)
)engine=InnoDB;
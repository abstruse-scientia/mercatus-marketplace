create table payment
(
    id                  bigint unsigned not null primary key auto_increment,

    -- points to the order the payment is tied to
    order_reference     varchar(255)    not null,


    amount              bigint not null,
    currency            varchar(3)      not null,

    -- provider metadata
    provider            varchar(40)     not null,
    provider_payment_id varchar(255),

    status              varchar(40)     not null,


    attempt_count       int             not null default 0,
    version             bigint          not null,

    created_at          DATETIME(6)     NOT NULL default current_timestamp(6),
    updated_at          DATETIME(6)     not null
                                                 default current_timestamp(6)
                                                 on update current_timestamp(6),

    constraint chk_payment_amt_to_be_positive check ( amount > 0  ),
    constraint uq_order_reference unique(order_reference),
    constraint uq_payment_provider_id unique (provider_payment_id)
)engine=InnoDB;

create index idx_payment_status on payment(status);








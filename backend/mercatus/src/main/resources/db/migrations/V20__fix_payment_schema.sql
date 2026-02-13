alter table payment drop column amount;

alter table payment drop index uq_order_reference;

create index idx_payment_order_ref on payment(order_reference);

alter table payment add column provider_order_id varchar(255);

alter table payment add column amount_expected bigint not null;

alter table payment add column amount_received bigint;

alter table payment drop index uq_payment_provider_id;

alter table payment add constraint uq_payment_provider_attempt unique (provider, provider_payment_id);

create index idx_payment_provider_order on payment(provider, provider_order_id);
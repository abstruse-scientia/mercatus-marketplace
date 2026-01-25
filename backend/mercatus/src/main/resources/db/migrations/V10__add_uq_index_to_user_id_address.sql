alter table address
add column default_active_user bigint
generated always as (
    case
        when is_default = true and is_active = true
        then user_id
        else null
    end
) stored;

create unique index uq_idx_user_default_address on address (default_active_user);
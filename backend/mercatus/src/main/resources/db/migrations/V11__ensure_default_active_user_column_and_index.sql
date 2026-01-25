ALTER TABLE address
    DROP COLUMN default_active_user;

ALTER TABLE address
    ADD COLUMN default_active_user BIGINT
        GENERATED ALWAYS AS (
            CASE
                WHEN is_default = TRUE AND is_active = TRUE
                    THEN user_id
                ELSE NULL
                END
            ) STORED;

CREATE UNIQUE INDEX uq_idx_user_default_address
    ON address (default_active_user);

CREATE TABLE refresh_token (
                               id BINARY(16) PRIMARY KEY,
                               token_hash VARCHAR(255) NOT NULL UNIQUE,
                               user_id BIGINT UNSIGNED NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                               expiry_date TIMESTAMP NOT NULL,
                               is_revoked BOOLEAN NOT NULL,
                               CONSTRAINT fk_refresh_token_user
                                   FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

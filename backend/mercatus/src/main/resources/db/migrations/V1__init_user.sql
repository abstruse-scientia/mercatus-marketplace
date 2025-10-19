CREATE TABLE IF NOT EXISTS users (
                                     user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     email VARCHAR(50) NOT NULL UNIQUE,
                                     password_hash VARCHAR(500),
                                     created_at    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                     updated_at    TIMESTAMP   DEFAULT NULL
);

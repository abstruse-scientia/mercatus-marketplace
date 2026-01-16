CREATE TABLE IF NOT EXISTS users (
                                     user_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                     user_name VARCHAR(100) NOT NULL ,
                                     email VARCHAR(50) NOT NULL UNIQUE,
                                     password_hash VARCHAR(500),
                                     created_at datetime(6) not null,
                                     created_by bigint unsigned,
                                     updated_at datetime(6),
                                     updated_by bigint unsigned

)ENGINE=InnoDB;
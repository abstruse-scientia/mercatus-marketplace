CREATE TABLE IF NOT EXISTS roles (
                                     role_id BIGINT unsigned AUTO_INCREMENT PRIMARY KEY ,
                                     name VARCHAR(50),
                                     created_at    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                     created_by    BIGINT UNSIGNED ,
                                     updated_at    TIMESTAMP   DEFAULT NULL,
                                     updated_by    BIGINT UNSIGNED
) engine=InnoDB;



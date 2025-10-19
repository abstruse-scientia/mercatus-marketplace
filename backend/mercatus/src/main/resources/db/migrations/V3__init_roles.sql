CREATE TABLE IF NOT EXISTS roles (
                                     role_id BIGINT AUTO_INCREMENT PRIMARY KEY ,
                                     name VARCHAR(50),
                                     created_at    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                     created_by    VARCHAR(20)                           NOT NULL,
                                     updated_at    TIMESTAMP   DEFAULT NULL,
                                     updated_by    VARCHAR(20) DEFAULT NULL
);

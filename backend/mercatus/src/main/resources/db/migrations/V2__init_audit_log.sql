CREATE TABLE IF NOT EXISTS audit_log (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         entity_name VARCHAR(255) NOT NULL ,
                                         entity_id BIGINT NOT NULL ,
                                         action VARCHAR(255) NOT NULL ,
                                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                         created_by BIGINT,
                                         updated_at TIMESTAMP NULL,
                                         updated_by BIGINT,
                                         FOREIGN KEY (created_by) REFERENCES users(user_id),
                                         FOREIGN KEY (updated_by) REFERENCES users(user_id)
);

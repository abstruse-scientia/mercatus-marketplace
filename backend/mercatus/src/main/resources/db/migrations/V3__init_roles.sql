CREATE TABLE IF NOT EXISTS roles (
                                     role_id BIGINT unsigned AUTO_INCREMENT PRIMARY KEY ,
                                     name VARCHAR(50),
                                     created_at    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                     created_by    BIGINT UNSIGNED    NOT NULL,
                                     updated_at    TIMESTAMP   DEFAULT NULL,
                                     updated_by    BIGINT UNSIGNED DEFAULT NULL
) engine=InnoDB;

alter table roles
    add constraint fk_roles_created_by
        foreign key (created_by)
            references users(user_id);

alter table roles
    add constraint fk_roles_updated_by
        foreign key (updated_by)
            references users(user_id);

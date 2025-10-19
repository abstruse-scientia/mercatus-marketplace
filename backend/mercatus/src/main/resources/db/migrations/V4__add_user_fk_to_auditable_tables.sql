ALTER TABLE users ADD COLUMN created_by BIGINT;
ALTER TABLE users ADD COLUMN updated_by BIGINT;

ALTER TABLE user_roles ADD COLUMN created_by BIGINT;
ALTER TABLE user_roles ADD COLUMN updated_by BIGINT;

ALTER TABLE refresh_token ADD COLUMN created_by BIGINT;
ALTER TABLE refresh_token ADD COLUMN updated_by BIGINT;

ALTER TABLE roles ADD COLUMN created_by BIGINT;
ALTER TABLE roles ADD COLUMN updated_by BIGINT;



ALTER TABLE users
    ADD CONSTRAINT fk_user_created_by FOREIGN KEY (created_by) REFERENCES users(user_id),
    ADD CONSTRAINT fk_user_updated_by FOREIGN KEY (updated_by) REFERENCES users(user_id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_created_by FOREIGN KEY (created_by) REFERENCES users(user_id),
    ADD CONSTRAINT fk_user_roles_updated_by FOREIGN KEY (updated_by) REFERENCES users(user_id);

ALTER TABLE refresh_token
    ADD CONSTRAINT fk_product_created_by FOREIGN KEY (created_by) REFERENCES users(user_id),
    ADD CONSTRAINT fk_product_updated_by FOREIGN KEY (updated_by) REFERENCES users(user_id);

ALTER TABLE roles
    ADD CONSTRAINT fk_user_created_by FOREIGN KEY (created_by) REFERENCES users(user_id),
    ADD CONSTRAINT fk_user_updated_by FOREIGN KEY (updated_by) REFERENCES users(user_id);
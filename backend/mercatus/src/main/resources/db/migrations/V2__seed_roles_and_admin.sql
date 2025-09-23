
# Seed roles
INSERT INTO roles(name, created_at, created_by)
VALUES ('ROLE_ADMIN', CURRENT_TIMESTAMP(), 'DBA');

INSERT INTO roles(name, created_at, created_by)
VALUES ('ROLE_USER', CURRENT_TIMESTAMP(), 'DBA');

# Seed super admin

INSERT INTO users(email, password_hash, created_at, created_by)
VALUES ('abstruseScientia@gmail.com', '$2a$12$r7d0U.6vvRwOAfUEhT1mXuUZBe6qSIcWwt.OjG0/RUqFk3U7Z.ngW',
        CURRENT_TIMESTAMP(), 'DBA');

# Seed role to super admin

INSERT INTO user_roles (user_id, role_id)
VALUES (1, 1)



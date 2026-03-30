-- Add is_active column to users table for account deactivation support
ALTER TABLE users ADD COLUMN is_active TINYINT(1) NOT NULL DEFAULT 1;

-- Create index for filtering active users
CREATE INDEX  idx_users_active ON users(is_active);


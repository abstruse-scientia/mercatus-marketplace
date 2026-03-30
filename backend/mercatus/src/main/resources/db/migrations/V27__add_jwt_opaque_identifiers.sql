-- Phase 1: Add opaque identifier column to users table
ALTER TABLE users ADD COLUMN opaque_identifier VARCHAR(36) UNIQUE;


-- Backfill Existing Data
-- Populate existing users with opaque identifiers (UUID)
UPDATE users SET opaque_identifier = UUID() WHERE opaque_identifier IS NULL;

-- Add NOT NULL constraint after populating
ALTER TABLE users MODIFY COLUMN opaque_identifier VARCHAR(36) NOT NULL UNIQUE;

-- Create index for fast opaque identifier lookup
CREATE INDEX idx_users_opaque_identifier ON users(opaque_identifier);

-- Update refresh_token table to support opaque identifiers
ALTER TABLE refresh_token ADD COLUMN opaque_identifier VARCHAR(36);

-- Add audit log column for opaque identifiers (if audit_logs table exists)
ALTER TABLE audit_log ADD COLUMN user_opaque_identifier VARCHAR(36);
CREATE INDEX idx_audit_opaque ON audit_log(user_opaque_identifier);


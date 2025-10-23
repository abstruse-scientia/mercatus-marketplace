ALTER TABLE cart
ADD COLUMN session_id varchar(100) NULL,
ADD COLUMN status varchar(20) NOT NULL DEFAULT 'ACTIVE';
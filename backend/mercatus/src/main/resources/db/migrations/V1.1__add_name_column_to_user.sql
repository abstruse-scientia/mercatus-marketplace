ALTER TABLE users
ADD COLUMN name VARCHAR(100) NOT NULL AFTER user_id;


UPDATE users
SET name = 'creator'
WHERE user_id = 1;

UPDATE users
SET name = 'shreyansh'
WHERE user_id = 2;

UPDATE users
SET name = 'priyanshu'
WHERE user_id = 3;
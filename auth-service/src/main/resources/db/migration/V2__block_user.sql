ALTER TABLE user_ndfl.users
    ADD block bool NULL;
UPDATE user_ndfl.users
SET block = false
WHERE true;

ALTER TABLE user_ndfl.users
    ALTER COLUMN "block" SET NOT NULL;
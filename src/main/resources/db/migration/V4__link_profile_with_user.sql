-- Add user_id column to profile table
ALTER TABLE profile
    ADD COLUMN user_id BIGINT NOT NULL UNIQUE;

-- Add foreign key constraint to link profile → users
ALTER TABLE profile
    ADD CONSTRAINT fk_profile_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE;

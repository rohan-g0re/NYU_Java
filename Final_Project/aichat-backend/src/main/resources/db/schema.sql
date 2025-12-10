-- Create database (run manually)
-- CREATE DATABASE ai_chat;

-- Create app_user table
CREATE TABLE IF NOT EXISTS app_user (
    id SERIAL PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    pass_hash TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_user_username ON app_user(username);

-- Create conversation table
CREATE TABLE IF NOT EXISTS conversation (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES app_user(id),
    title TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now(),
    head_message_id BIGINT NULL,
    last_message_id BIGINT NULL,
    is_deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_conv_user ON conversation(user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_conv_deleted ON conversation(user_id, is_deleted) WHERE is_deleted = FALSE;

-- Create message table
CREATE TABLE IF NOT EXISTS message (
    id BIGSERIAL PRIMARY KEY,
    conv_id INT NOT NULL REFERENCES conversation(id),
    role TEXT CHECK (role IN ('USER','ASSISTANT')) NOT NULL,
    content TEXT NOT NULL,
    ts TIMESTAMPTZ DEFAULT now(),
    prev_message_id BIGINT NULL REFERENCES message(id),
    next_message_id BIGINT NULL REFERENCES message(id)
);

CREATE INDEX IF NOT EXISTS idx_message_conv_ts ON message(conv_id, ts ASC, id ASC);
CREATE INDEX IF NOT EXISTS idx_message_prev ON message(conv_id, prev_message_id);
CREATE INDEX IF NOT EXISTS idx_message_next ON message(conv_id, next_message_id);


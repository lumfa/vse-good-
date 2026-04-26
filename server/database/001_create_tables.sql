CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TYPE category_type AS ENUM ('POSITIVE', 'NEGATIVE', 'NEUTRAL');

CREATE TABLE users (
    id            SERIAL PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    name          VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE categories (
    id            SERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    type          category_type NOT NULL DEFAULT 'NEUTRAL',
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE emotions (
    id            SERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    icon          VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE emotion_entries (
    id            SERIAL PRIMARY KEY,
    date          DATE         NOT NULL,
    note          TEXT,
    user_id       INT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id   INT          NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
    emotion_id    INT          NOT NULL REFERENCES emotions(id) ON DELETE RESTRICT,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE notifications (
    id            SERIAL PRIMARY KEY,
    message       VARCHAR(500) NOT NULL,
    date          DATE         NOT NULL,
    user_id       INT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    is_read       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_emotion_entries_user_id    ON emotion_entries(user_id);
CREATE INDEX idx_emotion_entries_date       ON emotion_entries(date);
CREATE INDEX idx_emotion_entries_emotion_id ON emotion_entries(emotion_id);
CREATE INDEX idx_emotion_entries_category_id ON emotion_entries(category_id);
CREATE INDEX idx_notifications_user_id      ON notifications(user_id);
CREATE INDEX idx_notifications_date         ON notifications(date);

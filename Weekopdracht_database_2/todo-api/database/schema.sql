CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS todo_lists (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_todo_list_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS todo_items (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    deadline TIMESTAMPTZ NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_todo_item_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS list_items (
    list_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,

    PRIMARY KEY (list_id, item_id),

    CONSTRAINT fk_list_items_list
        FOREIGN KEY (list_id)
        REFERENCES todo_lists(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_list_items_item
        FOREIGN KEY (item_id)
        REFERENCES todo_items(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_todo_lists_user_id
    ON todo_lists(user_id);

CREATE INDEX IF NOT EXISTS idx_todo_items_user_id
    ON todo_items(user_id);

CREATE INDEX IF NOT EXISTS idx_list_items_item_id
    ON list_items(item_id);
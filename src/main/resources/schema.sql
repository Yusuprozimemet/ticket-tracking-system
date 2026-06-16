CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS projects(
    project_id SERIAL PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS tickets(
    ticket_id SERIAL PRIMARY KEY,
    project_id INT NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    status TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,

    FOREIGN KEY (project_id)
        REFERENCES projects (project_id)
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS ticket_assignees(
    user_id INT NOT NULL,
    ticket_id INT NOT NULL,

    PRIMARY KEY(ticket_id, user_id),

    FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON DELETE CASCADE,

    FOREIGN KEY (ticket_id)
        REFERENCES tickets (ticket_id)
        ON DELETE CASCADE
);

-- Index the foreign keys used in joins/filters. PKs and UNIQUE(email) are auto-indexed;
-- ticket_assignees(ticket_id) is already covered by the composite PK (ticket_id, user_id).
CREATE INDEX IF NOT EXISTS idx_tickets_project_id       ON tickets (project_id);
CREATE INDEX IF NOT EXISTS idx_ticket_assignees_user_id ON ticket_assignees (user_id);

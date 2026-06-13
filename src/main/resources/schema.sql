CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE
);

CREATE TABLE projects(
    project_id SERIAL PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE tickets(
    ticket_id SERIAL PRIMARY KEY,
    project_id INT NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    status TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,

    FOREIGN KEY (project_id)
        REFERENCES projects (project_id)
        ON DELETE RESTRICT
);

CREATE TABLE ticket_assignees(
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


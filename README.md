# Ticket Tracking System

A REST API for tracking tickets across projects — built with Spring Boot and PostgreSQL.
Users can create projects and tickets, assign people to tickets, search and filter
tickets, and get email notifications whenever a ticket changes.

## Features

- **Users** — create, update, delete and list users (unique email, validated input).
- **Projects** — list all projects with per-status ticket counts (open / in progress / closed).
- **Tickets** — create, update, fetch and search tickets; filter by free-text search and status.
- **Assignees** — add and remove users on a ticket.
- **Email notifications** — assignees are notified via [Resend](https://resend.com) whenever a
  ticket changes (update, add/remove assignee). Sending failures are logged, never fatal.


## Getting started

### Prerequisites

- JDK 25
- PostgreSQL 16
- A [Resend](https://resend.com) API key (for email notifications)

### Configuration

Copy the example env file and fill in your own values:

```bash
cp .env.example .env
```

```properties
DB_URL=jdbc:postgresql://localhost:5432/tickets
DB_USER=your-user
DB_PASSWORD=your-password

RESEND_API_KEY=your-resend-key
RESEND_FROM_EMAIL=onboarding@resend.dev
```

The app loads `.env` automatically in local development. In CI/production these are
provided as real environment variables instead.

### Run

```bash
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`.

### API documentation (Swagger)

Interactive API docs are generated automatically by springdoc-openapi:

| URL | Description |
| --- | --- |
| `http://localhost:8080/swagger-ui.html` | Interactive Swagger UI |


### Test

```bash
./mvnw clean package
```

## API overview

| Method | Path | Description |
| --- | --- | --- |
| POST | `/api/users` | Create a new user |
| PUT | `/api/users/{id}` | Update an existing user |
| DELETE | `/api/users/{id}` | Delete a user |
| GET | `/api/users` | List all users |
| GET | `/api/users/{id}` | Get a single user |
| GET | `/api/projects` | List all projects (with per-status counts) |
| POST | `/api/tickets` | Create a new ticket |
| PUT | `/api/tickets/{id}` | Update a ticket |
| POST | `/api/tickets/{id}/assignees` | Add an assignee |
| DELETE | `/api/tickets/{id}/assignees/{userId}` | Remove an assignee |
| GET | `/api/tickets/{id}` | Get a single ticket |
| GET | `/api/tickets?search=...&status=...` | Search / filter tickets |

All requests and responses are JSON. Request bodies are validated; invalid input is
rejected before anything is saved.


## Documentation

| Document | Contents |
| --- | --- |
| [docs/API_design_document.md](docs/API_design_document.pdf) | Endpoint summary, full request/response shapes, validation rules, email behavior |

Interactive, always-up-to-date docs are also available via Swagger UI while the app is
running (see [API documentation](#api-documentation-swagger) above).
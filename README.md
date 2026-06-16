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

## Tech stack

- Java 25, Spring Boot 4.1
- Spring MVC (`@RestController`) on embedded Tomcat
- Spring JDBC (`JdbcTemplate`) over PostgreSQL
- Bean Validation for request DTOs
- springdoc-openapi (Swagger UI + OpenAPI 3 docs)
- Lombok
- JUnit 5 for tests
- GitHub Actions CI (build + test against a Postgres service)

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
| `http://localhost:8080/v3/api-docs` | Raw OpenAPI 3 spec (JSON) |

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

### Status codes

| Code | When |
| --- | --- |
| `200 OK` | Successful read, update, or add-assignee |
| `201 Created` | User or ticket created |
| `204 No Content` | User deleted, or assignee removed |
| `400 Bad Request` | Request body fails validation (e.g. blank title, name < 3 chars, invalid email) |
| `404 Not Found` | Referenced user, ticket, project, or assignment does not exist |
| `409 Conflict` | Email already in use, or user already assigned to the ticket |

Validation errors (`400`) return a map of `field → message`; other errors return
`{ "error": "<message>" }`.

## Documentation

| Document | Contents |
| --- | --- |
| [docs/API_design_document.md](docs/API_design_document.md) | Endpoint summary, full request/response shapes, validation rules, email behavior |
| [docs/ER_Diagram.md](docs/ER_Diagram.md) | Database schema — tables, columns, keys, relationships |
| [docs/HTTP_status_codes.md](docs/HTTP_status_codes.md) | Status codes returned per endpoint and per error |

Interactive, always-up-to-date docs are also available via Swagger UI while the app is
running (see [API documentation](#api-documentation-swagger) above).
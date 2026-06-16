# HTTP status codes

Status codes the API actually returns, taken from the controllers and the
`GlobalExceptionHandler`. Success codes come from each endpoint's
`@ResponseStatus`; error codes are mapped centrally from exceptions.

## Success codes per endpoint

| Method | Path | Success | Notes |
| --- | --- | --- | --- |
| POST | /api/users | 201 Created | Returns the created user |
| PUT | /api/users/{id} | 200 OK | Returns the updated user |
| DELETE | /api/users/{id} | 204 No Content | No body |
| GET | /api/users | 200 OK | List (empty `[]` if none) |
| GET | /api/users/{id} | 200 OK | Single user |
| GET | /api/projects | 200 OK | List with per-status counts |
| POST | /api/tickets | 201 Created | Returns the created ticket |
| PUT | /api/tickets/{id} | 200 OK | Returns the updated ticket |
| GET | /api/tickets/{id} | 200 OK | Single ticket |
| GET | /api/tickets | 200 OK | List (empty `[]` if none match) |
| POST | /api/tickets/{id}/assignees | 200 OK | Returns the ticket with updated assignees |
| DELETE | /api/tickets/{id}/assignees/{userId} | 204 No Content | No body |

## Error codes (GlobalExceptionHandler)

| Status | When | Triggered by |
| --- | --- | --- |
| 400 Bad Request | Request body fails bean validation (e.g. blank title, name < 3 chars, invalid email) | `MethodArgumentNotValidException` |
| 404 Not Found | Referenced user, ticket, project, or assignment does not exist | `UserNotFoundException`, `TicketNotFoundException`, `ProjectNotFoundException`, `AssignmentNotFoundException` |
| 409 Conflict | Email already in use, or user already assigned to the ticket | `DuplicateEmailException`, `AssignmentAlreadyExistsException` |

All error responses are JSON. Validation errors (400) return a map of
`field -> message`; the other handlers return `{ "error": "<message>" }`.

### Notes
- Duplicate email and "already assigned" return **409 Conflict** (not 400).
- An invalid `status` query value on `GET /api/tickets?status=...` is not handled
  explicitly; Spring's default enum-conversion failure results in **400 Bad Request**.
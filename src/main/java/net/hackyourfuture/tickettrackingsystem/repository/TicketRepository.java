package net.hackyourfuture.tickettrackingsystem.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import net.hackyourfuture.tickettrackingsystem.dto.requests.TicketRequest;
import net.hackyourfuture.tickettrackingsystem.model.Status;
import net.hackyourfuture.tickettrackingsystem.model.Ticket;

@Repository
public class TicketRepository {

    private final JdbcTemplate jdbcTemplate;

    public TicketRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Insert a new ticket and return it (with its new id and created_at).
    public Ticket createTicket(TicketRequest request) {
        String sql = """
                INSERT INTO tickets (project_id, title, description, status)
                VALUES (?, ?, ?, ?)
                RETURNING ticket_id, project_id, title, description, status, created_at, updated_at
                """;

        return jdbcTemplate.query(sql, rs -> {
            rs.next();
            return mapTicket(rs);
        }, request.getProjectId(), request.getTitle(), request.getDescription(), request.getStatus().name());
    }

    // Update a ticket and return it. Empty if no ticket has that id.
    public Optional<Ticket> updateTicket(long ticketId, TicketRequest request) {
        String sql = """
                UPDATE tickets
                SET title = ?, description = ?, project_id = ?, status = ?, updated_at = NOW()
                WHERE ticket_id = ?
                RETURNING ticket_id, project_id, title, description, status, created_at, updated_at
                """;

        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                return Optional.of(mapTicket(rs));
            }
            return Optional.empty();
        }, request.getTitle(), request.getDescription(), request.getProjectId(),
                request.getStatus().name(), ticketId);
    }

    // Find one ticket by id. Empty if it does not exist.
    public Optional<Ticket> fetchTicketById(long ticketId) {
        String sql = """
                SELECT ticket_id, project_id, title, description, status, created_at, updated_at
                FROM tickets
                WHERE ticket_id = ?
                """;

        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                return Optional.of(mapTicket(rs));
            }
            return Optional.empty();
        }, ticketId);
    }

    // Search tickets ([] if none match). 'search' filters title/description; 'status' is optional.
    // One fixed query per filter combination 
    public List<Ticket> searchTickets(String search, Status status) {
        boolean hasSearch = search != null && !search.isBlank();
        boolean hasStatus = status != null;
        String like = "%" + search + "%";

        if (hasSearch && hasStatus) {
            String sql = """
                    SELECT ticket_id, project_id, title, description, status, created_at, updated_at
                    FROM tickets
                    WHERE (LOWER(title) LIKE LOWER(?) OR LOWER(description) LIKE LOWER(?))
                      AND status = ?
                    ORDER BY ticket_id
                    """;
            return jdbcTemplate.query(sql, (rs, rowNum) -> mapTicket(rs), like, like, status.name());
        }
        if (hasSearch) {
            String sql = """
                    SELECT ticket_id, project_id, title, description, status, created_at, updated_at
                    FROM tickets
                    WHERE LOWER(title) LIKE LOWER(?) OR LOWER(description) LIKE LOWER(?)
                    ORDER BY ticket_id
                    """;
            return jdbcTemplate.query(sql, (rs, rowNum) -> mapTicket(rs), like, like);
        }
        if (hasStatus) {
            String sql = """
                    SELECT ticket_id, project_id, title, description, status, created_at, updated_at
                    FROM tickets
                    WHERE status = ?
                    ORDER BY ticket_id
                    """;
            return jdbcTemplate.query(sql, (rs, rowNum) -> mapTicket(rs), status.name());
        }
        String sql = """
                SELECT ticket_id, project_id, title, description, status, created_at, updated_at
                FROM tickets
                ORDER BY ticket_id
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapTicket(rs));
    }

    // True if a ticket with this id exists.
    public boolean existsById(long ticketId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM tickets WHERE ticket_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, ticketId));
    }

    // Build a Ticket model from one database row.
    private Ticket mapTicket(ResultSet rs) throws SQLException {
        return Ticket.builder()
                .id(rs.getLong("ticket_id"))
                .projectId(rs.getLong("project_id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .status(Status.valueOf(rs.getString("status")))
                .createdAt(toInstant(rs.getObject("created_at", OffsetDateTime.class)))
                .updatedAt(toInstant(rs.getObject("updated_at", OffsetDateTime.class)))
                .build();
    }

    // TIMESTAMPTZ columns map to OffsetDateTime in pgjdbc; convert to Instant (null-safe,
    // since updated_at is null until a ticket is first updated).
    private static Instant toInstant(OffsetDateTime value) {
        return value == null ? null : value.toInstant();
    }
}

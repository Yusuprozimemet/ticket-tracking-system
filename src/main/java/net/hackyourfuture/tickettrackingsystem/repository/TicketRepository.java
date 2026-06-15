package net.hackyourfuture.tickettrackingsystem.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import net.hackyourfuture.tickettrackingsystem.dto.requests.TicketRequest;
import net.hackyourfuture.tickettrackingsystem.dto.responses.TicketResponse;
import net.hackyourfuture.tickettrackingsystem.model.Status;

@Repository
public class TicketRepository {

    private final JdbcTemplate jdbcTemplate;

    public TicketRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // id, title, description, projectId, status, createdAt, updatedAt(null), assignees([])
    public TicketResponse createTicket(TicketRequest request) {
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

    // Full updated ticket (updatedAt now set)
    public Optional<TicketResponse> updateTicket(long ticketId, TicketRequest request) {
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

    // Full ticket – id, title, description, projectId, status, createdAt, updatedAt
    public Optional<TicketResponse> fetchTicketById(long ticketId) {
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

    // List of tickets ([] if none match). search filters title/description; status is optional.
    public List<TicketResponse> searchTickets(String search, Status status) {
        StringBuilder sql = new StringBuilder("""
                SELECT ticket_id, project_id, title, description, status, created_at, updated_at
                FROM tickets
                WHERE 1 = 1
                """);
        List<Object> params = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            sql.append(" AND (title ILIKE ? OR description ILIKE ?)");
            String like = "%" + search + "%";
            params.add(like);
            params.add(like);
        }
        if (status != null) {
            sql.append(" AND status = ?");
            params.add(status.name());
        }
        sql.append(" ORDER BY ticket_id");

        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> mapTicket(rs), params.toArray());
    }

    public boolean existsById(long ticketId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM tickets WHERE ticket_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, ticketId));
    }

    private TicketResponse mapTicket(ResultSet rs) throws SQLException {
        return TicketResponse.builder()
                .id(rs.getLong("ticket_id"))
                .projectId(rs.getLong("project_id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .status(Status.valueOf(rs.getString("status")))
                .createdAt(rs.getObject("created_at", LocalDateTime.class))
                .updatedAt(rs.getObject("updated_at", LocalDateTime.class))
                .build();
    }
}
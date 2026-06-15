package net.hackyourfuture.tickettrackingsystem.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AssigneeRepository {

    private final JdbcTemplate jdbcTemplate;

    public AssigneeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Long> fetchAssigneeIds(long ticketId) {
        String sql = """
                SELECT user_id
                FROM ticket_assignees
                WHERE ticket_id = ?
                ORDER BY user_id
                """;
        return jdbcTemplate.queryForList(sql, Long.class, ticketId);
    }

    public boolean existsAssignment(long ticketId, long userId) {
        String sql = """
                SELECT EXISTS(
                    SELECT 1 FROM ticket_assignees
                    WHERE ticket_id = ? AND user_id = ?
                )
                """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, ticketId, userId));
    }

    public void addAssignee(long ticketId, long userId) {
        String sql = "INSERT INTO ticket_assignees (ticket_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, ticketId, userId);
    }

    // Returns the number of rows removed (0 means the assignment did not exist).
    public int removeAssignee(long ticketId, long userId) {
        String sql = "DELETE FROM ticket_assignees WHERE ticket_id = ? AND user_id = ?";
        return jdbcTemplate.update(sql, ticketId, userId);
    }
}

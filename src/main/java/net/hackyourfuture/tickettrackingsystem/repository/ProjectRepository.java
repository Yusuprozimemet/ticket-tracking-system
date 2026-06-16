package net.hackyourfuture.tickettrackingsystem.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import net.hackyourfuture.tickettrackingsystem.dto.responses.ProjectSummary;

@Repository
public class ProjectRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // List of { id, name, ticketCounts: { open, in progress, closed } }
    public List<ProjectSummary> fetchAllProjectSummaries() {
        String sql = """
                SELECT projects.project_id,
                       projects.name,
                       COUNT(*) FILTER (WHERE tickets.status = 'OPEN')        AS open_count,
                       COUNT(*) FILTER (WHERE tickets.status = 'IN_PROGRESS') AS in_progress_count,
                       COUNT(*) FILTER (WHERE tickets.status = 'CLOSED')      AS closed_count
                FROM projects
                LEFT JOIN tickets ON tickets.project_id = projects.project_id
                GROUP BY projects.project_id, projects.name
                ORDER BY projects.project_id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> ProjectSummary.builder()
                .projectId(rs.getLong("project_id"))
                .name(rs.getString("name"))
                .openCount(rs.getLong("open_count"))
                .inProgressCount(rs.getLong("in_progress_count"))
                .closedCount(rs.getLong("closed_count"))
                .build());
    }

    // True if a project with this id exists.
    public boolean existsById(long projectId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM projects WHERE project_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, projectId));
    }
}
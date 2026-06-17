package net.hackyourfuture.tickettrackingsystem.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import net.hackyourfuture.tickettrackingsystem.client.ResendClient;
import net.hackyourfuture.tickettrackingsystem.client.ResendClient.ResendEmailRequest;

// Integration tests: real HTTP requests (MockMvc) through controller → service → repository
// → real PostgreSQL (Testcontainers, from IntegrationTestBase). Resend is mocked so no
// real email leaves the machine.
class ApiEndpointTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Replace the real Resend client (the external email API) with a mock for these tests.
    @MockitoBean
    private ResendClient resendClient;

    @Test
    void getUserById_returnsUser() throws Exception {
        // Arrange: seed a user straight into the database
        Long userId = jdbcTemplate.queryForObject(
                "INSERT INTO users (name, email) VALUES (?, ?) RETURNING user_id",
                Long.class, "Yusup", "yusup@example.com");

        // Act + Assert: GET it back through the real HTTP endpoint
        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("yusup@example.com"));
    }

    @Test
    void createTicket_returns201() throws Exception {
        // Arrange: a ticket needs a project to exist first (foreign key)
        Long projectId = jdbcTemplate.queryForObject(
                "INSERT INTO projects (name) VALUES (?) RETURNING project_id", Long.class, "Apollo");

        // Act + Assert: POST a new ticket
        mockMvc.perform(post("/api/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "title": "Login bug",
                            "description": "Cannot log in",
                            "status": "OPEN",
                            "projectId": %d
                        }
                        """.formatted(projectId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Login bug"));
    }

    @Test
    void addAssignee_returns200_andSendsEmail() throws Exception {
        // Arrange: seed a project, a ticket, and a user
        Long projectId = jdbcTemplate.queryForObject(
                "INSERT INTO projects (name) VALUES (?) RETURNING project_id", Long.class, "Gemini");
        Long ticketId = jdbcTemplate.queryForObject(
                "INSERT INTO tickets (project_id, title, status) VALUES (?, ?, ?) RETURNING ticket_id",
                Long.class, projectId, "Bug", "OPEN");
        Long userId = jdbcTemplate.queryForObject(
                "INSERT INTO users (name, email) VALUES (?, ?) RETURNING user_id",
                Long.class, "Yusup", "yusup@example.com");

        // Act + Assert: assigning a user returns 200 and triggers an email
        mockMvc.perform(post("/api/tickets/" + ticketId + "/assignees")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\": " + userId + "}"))
                .andExpect(status().isOk());

        // The mocked Resend client was asked to send one email (no real network call happened)
        verify(resendClient).sendEmail(any(ResendEmailRequest.class));
    }
}

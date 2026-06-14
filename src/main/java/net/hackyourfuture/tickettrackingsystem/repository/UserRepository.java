package net.hackyourfuture.tickettrackingsystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import net.hackyourfuture.tickettrackingsystem.dto.requests.UserRequest;
import net.hackyourfuture.tickettrackingsystem.dto.responses.UserResponse;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public UserResponse createUser(UserRequest requestBody) {
        String sql = """
                INSERT INTO users (name, email)
                VALUES (?, ?)
                RETURNING user_id, name, email
                """;

        return jdbcTemplate.query(sql, rs -> {
            rs.next();
            return UserResponse.builder()
                    .id(rs.getLong("user_id"))
                    .name(rs.getString("name"))
                    .email(rs.getString("email"))
                    .build();
        }, requestBody.getName(), requestBody.getEmail());
    }

    public Optional<UserResponse> updateUser(long userId, UserRequest requestBody) {
        String sql = """
                UPDATE users
                SET name = ?, email = ?
                WHERE user_id = ?
                RETURNING user_id, name, email
                """;

        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                return Optional.of(UserResponse.builder()
                        .id(rs.getLong("user_id"))
                        .name(rs.getString("name"))
                        .email(rs.getString("email"))
                        .build());
            }
            return Optional.empty();
        }, requestBody.getName(), requestBody.getEmail(), userId);
    }

    public void removeUser(long userId) {
        String sql = """
                DELETE FROM users
                WHERE user_id = ?
                """;
        jdbcTemplate.update(sql, userId);
    }

    public List<UserResponse> fetchAllUsers() {
        String sql = """
                SELECT user_id, name, email
                FROM users
                ORDER BY user_id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> UserResponse.builder()
                .id(rs.getLong("user_id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .build());
    }

    public Optional<UserResponse> fetchUserById(long userId) {
        String sql = """
                SELECT user_id, name, email
                FROM users
                WHERE user_id = ?
                """;

        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                return Optional.of(UserResponse.builder()
                        .id(rs.getLong("user_id"))
                        .name(rs.getString("name"))
                        .email(rs.getString("email"))
                        .build());
            }
            return Optional.empty();
        }, userId);
    }

    
}

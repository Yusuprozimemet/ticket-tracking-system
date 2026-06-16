package net.hackyourfuture.tickettrackingsystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import net.hackyourfuture.tickettrackingsystem.dto.requests.UserRequest;
import net.hackyourfuture.tickettrackingsystem.model.User;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Turn one database row into a User model.
    private final RowMapper<User> userMapper = (rs, rowNum) -> User.builder()
            .id(rs.getLong("user_id"))
            .name(rs.getString("name"))
            .email(rs.getString("email"))
            .build();

    // Insert a new user and return it (with its new id).
    public User createUser(UserRequest requestBody) {
        String sql = """
                INSERT INTO users (name, email)
                VALUES (?, ?)
                RETURNING user_id, name, email
                """;

        return jdbcTemplate.query(sql, rs -> {
            rs.next();
            return userMapper.mapRow(rs, 0);
        }, requestBody.getName(), requestBody.getEmail());
    }

    // Update a user and return it. Empty if no user has that id.
    public Optional<User> updateUser(long userId, UserRequest requestBody) {
        String sql = """
                UPDATE users
                SET name = ?, email = ?
                WHERE user_id = ?
                RETURNING user_id, name, email
                """;

        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                return Optional.of(userMapper.mapRow(rs, 0));
            }
            return Optional.empty();
        }, requestBody.getName(), requestBody.getEmail(), userId);
    }

    // Delete a user by id.
    public void removeUser(long userId) {
        String sql = """
                DELETE FROM users
                WHERE user_id = ?
                """;
        jdbcTemplate.update(sql, userId);
    }

    // Return all users.
    public List<User> fetchAllUsers() {
        String sql = """
                SELECT user_id, name, email
                FROM users
                ORDER BY user_id
                """;

        return jdbcTemplate.query(sql, userMapper);
    }

    // Find one user by id. Empty if it does not exist.
    public Optional<User> fetchUserById(long userId) {
        String sql = """
                SELECT user_id, name, email
                FROM users
                WHERE user_id = ?
                """;

        return jdbcTemplate.query(sql, userMapper, userId).stream().findFirst();
    }
}

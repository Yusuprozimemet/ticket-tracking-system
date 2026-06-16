package net.hackyourfuture.tickettrackingsystem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Domain model of a user row in the database (persistence shape).
// Services map this to UserResponse (the API shape) before returning it.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;
    private String name;
    private String email;
}

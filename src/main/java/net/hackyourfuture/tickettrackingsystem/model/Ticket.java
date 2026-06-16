package net.hackyourfuture.tickettrackingsystem.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Domain model of a ticket row in the database (persistence shape).
// Assignees live in a separate table, so they are not part of this model;
// services attach them when mapping to TicketResponse (the API shape).
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    private Long id;
    private Long projectId;
    private String title;
    private String description;
    private Status status;
    private Instant createdAt;
    private Instant updatedAt;
}

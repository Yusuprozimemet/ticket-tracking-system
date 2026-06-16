package net.hackyourfuture.tickettrackingsystem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// A link between a ticket and a user assigned to it.
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Assignee {
    private long ticketId;
    private long userId;
}

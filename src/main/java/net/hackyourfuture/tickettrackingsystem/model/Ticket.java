package net.hackyourfuture.tickettrackingsystem.model;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    private long id;
    private long projectId;
    private String title;
    private String description;
    private Status status;
    private Instant createdAt;
    private Instant updatedAt;
}
    


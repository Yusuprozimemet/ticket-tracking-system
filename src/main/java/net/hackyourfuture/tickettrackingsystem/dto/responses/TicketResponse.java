package net.hackyourfuture.tickettrackingsystem.dto.responses;

import java.time.Instant;
import net.hackyourfuture.tickettrackingsystem.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {

    private Long id;
    private Long projectId;
    private String title;
    private String description;
    private Status status;
    private Instant createdAt;
    private Instant updatedAt;
    
}

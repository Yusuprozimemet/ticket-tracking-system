package net.hackyourfuture.tickettrackingsystem.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Outgoing project row: the project plus its ticket counts per status.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSummary {
    private Long projectId;
    private String name;
    private Long openCount;
    private Long inProgressCount;
    private Long closedCount;
    
}

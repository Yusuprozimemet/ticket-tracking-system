package net.hackyourfuture.tickettrackingsystem.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


// Incoming body for adding an assignee: which user to assign.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssigneeRequest {

    @NotNull
    private Long userId;
}

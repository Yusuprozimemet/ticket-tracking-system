package net.hackyourfuture.tickettrackingsystem.dto.requests;


import net.hackyourfuture.tickettrackingsystem.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Incoming body for creating or updating a ticket.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Status status;

    @NotNull
    private Long projectId;

}

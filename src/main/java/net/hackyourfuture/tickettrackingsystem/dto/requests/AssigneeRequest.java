package net.hackyourfuture.tickettrackingsystem.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssigneeRequest {

    @NotNull
    private Long userId;
}

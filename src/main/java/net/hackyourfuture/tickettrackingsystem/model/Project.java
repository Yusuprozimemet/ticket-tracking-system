package net.hackyourfuture.tickettrackingsystem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// A project that tickets belong to.
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Project {
    private long id;
    private String name;
    
}

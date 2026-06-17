package net.hackyourfuture.tickettrackingsystem.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.hackyourfuture.tickettrackingsystem.dto.responses.TicketResponse;
import net.hackyourfuture.tickettrackingsystem.model.Status;
import net.hackyourfuture.tickettrackingsystem.model.Ticket;
import net.hackyourfuture.tickettrackingsystem.repository.AssigneeRepository;
import net.hackyourfuture.tickettrackingsystem.repository.ProjectRepository;
import net.hackyourfuture.tickettrackingsystem.repository.TicketRepository;
import net.hackyourfuture.tickettrackingsystem.repository.UserRepository;
import net.hackyourfuture.tickettrackingsystem.service.EmailService;
import net.hackyourfuture.tickettrackingsystem.service.TicketService;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock private TicketRepository ticketRepository;
    @Mock private AssigneeRepository assigneeRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private EmailService emailService;

    @InjectMocks private TicketService ticketService;

    @Test
    void getTicketById_returnsTicket_whenFound() {
        // Arrange
        Ticket ticket = Ticket.builder().id(1L).projectId(1L).title("Bug").status(Status.OPEN).build();
        when(ticketRepository.fetchTicketById(1L)).thenReturn(Optional.of(ticket));
        when(assigneeRepository.fetchAssigneeIds(1L)).thenReturn(List.of());

        // Act
        TicketResponse result = ticketService.getTicketById(1L);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
    }
}

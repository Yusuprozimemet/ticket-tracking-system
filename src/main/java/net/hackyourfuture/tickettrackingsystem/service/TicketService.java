package net.hackyourfuture.tickettrackingsystem.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.hackyourfuture.tickettrackingsystem.dto.requests.TicketRequest;
import net.hackyourfuture.tickettrackingsystem.dto.responses.TicketResponse;
import net.hackyourfuture.tickettrackingsystem.dto.responses.UserResponse;
import net.hackyourfuture.tickettrackingsystem.exception.AssignmentAlreadyExistsException;
import net.hackyourfuture.tickettrackingsystem.exception.AssignmentNotFoundException;
import net.hackyourfuture.tickettrackingsystem.exception.ProjectNotFoundException;
import net.hackyourfuture.tickettrackingsystem.exception.TicketNotFoundException;
import net.hackyourfuture.tickettrackingsystem.exception.UserNotFoundException;
import net.hackyourfuture.tickettrackingsystem.model.Status;
import net.hackyourfuture.tickettrackingsystem.repository.AssigneeRepository;
import net.hackyourfuture.tickettrackingsystem.repository.ProjectRepository;
import net.hackyourfuture.tickettrackingsystem.repository.TicketRepository;
import net.hackyourfuture.tickettrackingsystem.repository.UserRepository;

@Service
public class TicketService {

    private static final Logger log = LoggerFactory.getLogger(TicketService.class);

    private final TicketRepository ticketRepository;
    private final AssigneeRepository assigneeRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final EmailService emailService;

    public TicketService(TicketRepository ticketRepository, AssigneeRepository assigneeRepository,
            UserRepository userRepository, ProjectRepository projectRepository, EmailService emailService) {
        this.ticketRepository = ticketRepository;
        this.assigneeRepository = assigneeRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.emailService = emailService;
    }

    // Create a new ticket. No email is sent on creation.
    public TicketResponse createTicket(TicketRequest requestBody) {
        checkProjectExists(requestBody.getProjectId());

        TicketResponse ticket = ticketRepository.createTicket(requestBody);
        // No email on creation (per the design doc).
        return withAssignees(ticket);
    }

    // Update an existing ticket, then email its assignees.
    public TicketResponse updateTicket(long id, TicketRequest requestBody) {
        checkProjectExists(requestBody.getProjectId());

        TicketResponse ticket = ticketRepository.updateTicket(id, requestBody)
                .orElseThrow(() -> new TicketNotFoundException("Could not find a ticket with id " + id + "."));

        withAssignees(ticket);
        notifyAssignees(ticket, "updated");
        return ticket;
    }

    // Get one ticket by its id (404 if it does not exist).
    public TicketResponse getTicketById(long id) {
        TicketResponse ticket = ticketRepository.fetchTicketById(id)
                .orElseThrow(() -> new TicketNotFoundException("Could not find a ticket with id " + id + "."));

        return withAssignees(ticket);
    }

    // Search/filter tickets. Returns an empty list if none match.
    public List<TicketResponse> searchTickets(String search, Status status) {
        List<TicketResponse> tickets = ticketRepository.searchTickets(search, status);
        for (TicketResponse ticket : tickets) {
            withAssignees(ticket);
        }
        return tickets;
    }

    // Assign a user to a ticket, then email the assignees.
    public TicketResponse addAssignee(long id, Long userId) {
        if (!ticketRepository.existsById(id)) {
            throw new TicketNotFoundException("Could not find a ticket with id " + id + ".");
        }
        if (userRepository.fetchUserById(userId).isEmpty()) {
            throw new UserNotFoundException("Could not find a user with id " + userId + ".");
        }
        if (assigneeRepository.existsAssignment(id, userId)) {
            throw new AssignmentAlreadyExistsException(
                    "User " + userId + " is already assigned to ticket " + id + ".");
        }

        assigneeRepository.addAssignee(id, userId);

        TicketResponse ticket = ticketRepository.fetchTicketById(id)
                .orElseThrow(() -> new TicketNotFoundException("Could not find a ticket with id " + id + "."));
        withAssignees(ticket);
        notifyAssignees(ticket, "assignee added");
        return ticket;
    }

    // Remove a user from a ticket, then email the remaining assignees.
    public void removeAssignee(long id, long userId) {
        if (!ticketRepository.existsById(id)) {
            throw new TicketNotFoundException("Could not find a ticket with id " + id + ".");
        }

        int removed = assigneeRepository.removeAssignee(id, userId);
        if (removed == 0) {
            throw new AssignmentNotFoundException(
                    "User " + userId + " is not assigned to ticket " + id + ".");
        }

        TicketResponse ticket = ticketRepository.fetchTicketById(id)
                .orElseThrow(() -> new TicketNotFoundException("Could not find a ticket with id " + id + "."));
        withAssignees(ticket);
        notifyAssignees(ticket, "assignee removed");
    }

    // --- small helpers -----------------------------------------------------

    // Throw a 404 if the given project does not exist.
    private void checkProjectExists(long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException("Could not find a project with id " + projectId + ".");
        }
    }

    // Fill in the ticket's assignee ids so the response matches the API design.
    private TicketResponse withAssignees(TicketResponse ticket) {
        ticket.setAssignees(assigneeRepository.fetchAssigneeIds(ticket.getId()));
        return ticket;
    }

    // Email the ticket's current assignees. Failures are logged, never fatal.
    private void notifyAssignees(TicketResponse ticket, String action) {
        List<UserResponse> recipients = getRecipients(ticket.getId());
        if (recipients.isEmpty()) {
            return;
        }
        try {
            emailService.sendTicketChanged(ticket, action, recipients);
        } catch (Exception e) {
            log.error("Failed to send '{}' email for ticket {} to recipients {}",
                    action, ticket.getId(), recipients, e);
        }
    }

    // Look up the full user records for a ticket's assignees (used as email recipients).
    private List<UserResponse> getRecipients(long ticketId) {
        List<UserResponse> recipients = new ArrayList<>();
        for (Long userId : assigneeRepository.fetchAssigneeIds(ticketId)) {
            userRepository.fetchUserById(userId).ifPresent(recipients::add);
        }
        return recipients;
    }
}

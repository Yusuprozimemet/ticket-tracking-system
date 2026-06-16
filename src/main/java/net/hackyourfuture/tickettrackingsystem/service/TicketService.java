package net.hackyourfuture.tickettrackingsystem.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.hackyourfuture.tickettrackingsystem.dto.requests.TicketRequest;
import net.hackyourfuture.tickettrackingsystem.dto.responses.TicketResponse;
import net.hackyourfuture.tickettrackingsystem.dto.responses.UserResponse;
import net.hackyourfuture.tickettrackingsystem.exception.AssignmentAlreadyExistsException;
import net.hackyourfuture.tickettrackingsystem.exception.AssignmentNotFoundException;
import net.hackyourfuture.tickettrackingsystem.exception.ProjectNotFoundException;
import net.hackyourfuture.tickettrackingsystem.exception.TicketNotFoundException;
import net.hackyourfuture.tickettrackingsystem.exception.UserNotFoundException;
import net.hackyourfuture.tickettrackingsystem.model.Status;
import net.hackyourfuture.tickettrackingsystem.model.Ticket;
import net.hackyourfuture.tickettrackingsystem.model.User;
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
    @Transactional
    public TicketResponse createTicket(TicketRequest requestBody) {
        checkProjectExists(requestBody.getProjectId());

        Ticket ticket = ticketRepository.createTicket(requestBody);
        // No email on creation (per the design doc).
        return toResponse(ticket);
    }

    // Update an existing ticket, then email its assignees.
    @Transactional
    public TicketResponse updateTicket(long id, TicketRequest requestBody) {
        checkProjectExists(requestBody.getProjectId());

        Ticket ticket = ticketRepository.updateTicket(id, requestBody)
                .orElseThrow(() -> new TicketNotFoundException("Could not find a ticket with id " + id + "."));

        TicketResponse response = toResponse(ticket);
        notifyAssignees(response, "updated");
        return response;
    }

    // Get one ticket by its id (404 if it does not exist).
    public TicketResponse getTicketById(long id) {
        Ticket ticket = ticketRepository.fetchTicketById(id)
                .orElseThrow(() -> new TicketNotFoundException("Could not find a ticket with id " + id + "."));

        return toResponse(ticket);
    }

    // Search/filter tickets. Returns an empty list if none match.
    public List<TicketResponse> searchTickets(String search, Status status) {
        List<TicketResponse> responses = new ArrayList<>();
        for (Ticket ticket : ticketRepository.searchTickets(search, status)) {
            responses.add(toResponse(ticket));
        }
        return responses;
    }

    // Assign a user to a ticket, then email the assignees.
    @Transactional
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

        Ticket ticket = ticketRepository.fetchTicketById(id)
                .orElseThrow(() -> new TicketNotFoundException("Could not find a ticket with id " + id + "."));
        TicketResponse response = toResponse(ticket);
        notifyAssignees(response, "assignee added");
        return response;
    }

    // Remove a user from a ticket, then email the remaining assignees.
    @Transactional
    public void removeAssignee(long id, long userId) {
        if (!ticketRepository.existsById(id)) {
            throw new TicketNotFoundException("Could not find a ticket with id " + id + ".");
        }

        int removed = assigneeRepository.removeAssignee(id, userId);
        if (removed == 0) {
            throw new AssignmentNotFoundException(
                    "User " + userId + " is not assigned to ticket " + id + ".");
        }

        Ticket ticket = ticketRepository.fetchTicketById(id)
                .orElseThrow(() -> new TicketNotFoundException("Could not find a ticket with id " + id + "."));
        notifyAssignees(toResponse(ticket), "assignee removed");
    }

    // --- small helpers -----------------------------------------------------

    // Throw a 404 if the given project does not exist.
    private void checkProjectExists(long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException("Could not find a project with id " + projectId + ".");
        }
    }

    // Map the ticket model to the API response, attaching its assignee ids.
    private TicketResponse toResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .projectId(ticket.getProjectId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .assignees(assigneeRepository.fetchAssigneeIds(ticket.getId()))
                .build();
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
            userRepository.fetchUserById(userId)
                    .map(this::toUserResponse)
                    .ifPresent(recipients::add);
        }
        return recipients;
    }

    // Map a user model to the API response shape (used for email recipients).
    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}

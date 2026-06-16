package net.hackyourfuture.tickettrackingsystem.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import net.hackyourfuture.tickettrackingsystem.dto.requests.AssigneeRequest;
import net.hackyourfuture.tickettrackingsystem.dto.requests.TicketRequest;
import net.hackyourfuture.tickettrackingsystem.dto.responses.TicketResponse;
import net.hackyourfuture.tickettrackingsystem.model.Status;
import net.hackyourfuture.tickettrackingsystem.service.TicketService;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    // POST /api/tickets - create a new ticket.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse createTicket(@Valid @RequestBody TicketRequest requestBody) {
        return service.createTicket(requestBody);
    }

    // PUT /api/tickets/{id} - update an existing ticket.
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TicketResponse updateTicket(@PathVariable long id, @Valid @RequestBody TicketRequest requestBody) {
        return service.updateTicket(id, requestBody);
    }

    // GET /api/tickets/{id} - get one ticket by id.
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TicketResponse getTicketById(@PathVariable long id) {
        return service.getTicketById(id);
    }

    // GET /api/tickets?search=...&status=... - search/filter tickets.
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TicketResponse> searchTickets(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Status status) {
        return service.searchTickets(search, status);
    }

    // POST /api/tickets/{id}/assignees - assign a user to a ticket.
    @PostMapping("/{id}/assignees")
    @ResponseStatus(HttpStatus.OK)
    public TicketResponse addAssignee(@PathVariable long id, @Valid @RequestBody AssigneeRequest requestBody) {
        return service.addAssignee(id, requestBody.getUserId());
    }

    // DELETE /api/tickets/{id}/assignees/{userId} - remove a user from a ticket.
    @DeleteMapping("/{id}/assignees/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAssignee(@PathVariable long id, @PathVariable long userId) {
        service.removeAssignee(id, userId);
    }
}
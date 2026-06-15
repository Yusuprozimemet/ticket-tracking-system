package net.hackyourfuture.tickettrackingsystem.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import net.hackyourfuture.tickettrackingsystem.dto.requests.TicketRequest;
import net.hackyourfuture.tickettrackingsystem.dto.responses.TicketResponse;
import net.hackyourfuture.tickettrackingsystem.model.Status;

@Service
public class TicketService {

    public TicketResponse createTicket(TicketRequest requestBody) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createTicket'");
    }

    public TicketResponse updateTicket(long id, TicketRequest requestBody) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateTicket'");
    }

    public TicketResponse getTicketById(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTicketById'");
    }

    public List<TicketResponse> searchTickets(String search, Status status) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchTickets'");
    }

    public TicketResponse addAssignee(long id, Long userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addAssignee'");
    }
    
}

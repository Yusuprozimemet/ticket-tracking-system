package net.hackyourfuture.tickettrackingsystem.exception;

// A requested resource (user, project, ticket, assignment) does not exist. Maps to 404.
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}

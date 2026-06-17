package net.hackyourfuture.tickettrackingsystem.exception;

// A request conflicts with current state (duplicate email, assignment already exists). Maps to 409.
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}

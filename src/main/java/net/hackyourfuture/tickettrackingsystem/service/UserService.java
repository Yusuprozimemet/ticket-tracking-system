package net.hackyourfuture.tickettrackingsystem.service;

import net.hackyourfuture.tickettrackingsystem.dto.requests.UserRequest;
import net.hackyourfuture.tickettrackingsystem.dto.responses.UserResponse;
import net.hackyourfuture.tickettrackingsystem.exception.DuplicateEmailException;
import net.hackyourfuture.tickettrackingsystem.exception.UserNotFoundException;
import net.hackyourfuture.tickettrackingsystem.repository.UserRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository){
        this.repository = repository;
    }

    // Create a new user. Fails with 409 if the email is already taken.
    public UserResponse createUser(UserRequest requestBody){
        try {
            return repository.createUser(requestBody);
        } catch (DuplicateKeyException e) {
            throw new DuplicateEmailException("An account with this email already exists: " + requestBody.getEmail());
        }
    }

    // Update an existing user (404 if not found, 409 if the email is taken).
    public UserResponse updateUser(long userId, UserRequest requestBody){
        try {
            return repository.updateUser(userId, requestBody)
                    .orElseThrow(() -> new UserNotFoundException("Could not find a user with id " + userId + "."));
        } catch (DuplicateKeyException e) {
            throw new DuplicateEmailException("An account with this email already exists: " + requestBody.getEmail());
        }
    }

    // Delete a user by id (404 if not found).
    public void deleteUser(long userId){
        repository.fetchUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("Could not find a user with id " + userId + "."));

        repository.removeUser(userId);
    }

    // List all users.
    public List<UserResponse> getAllUsers(){
        return repository.fetchAllUsers();
    }

    // Get one user by id (404 if not found).
    public UserResponse getUserById(long userId){
        return repository.fetchUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("Could not find a user with id " + userId + "."));
    }

    
}

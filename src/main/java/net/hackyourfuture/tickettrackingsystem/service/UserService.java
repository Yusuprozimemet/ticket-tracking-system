package net.hackyourfuture.tickettrackingsystem.service;

import net.hackyourfuture.tickettrackingsystem.dto.requests.UserRequest;
import net.hackyourfuture.tickettrackingsystem.dto.responses.UserResponse;
import net.hackyourfuture.tickettrackingsystem.exception.ConflictException;
import net.hackyourfuture.tickettrackingsystem.exception.NotFoundException;
import net.hackyourfuture.tickettrackingsystem.model.User;
import net.hackyourfuture.tickettrackingsystem.repository.UserRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository){
        this.repository = repository;
    }

    // Create a new user. Fails with 409 if the email is already taken.
    @Transactional
    public UserResponse createUser(UserRequest requestBody){
        try {
            return toResponse(repository.createUser(requestBody));
        } catch (DuplicateKeyException e) {
            throw new ConflictException("An account with this email already exists: " + requestBody.getEmail());
        }
    }

    // Update an existing user (404 if not found, 409 if the email is taken).
    @Transactional
    public UserResponse updateUser(long userId, UserRequest requestBody){
        try {
            return repository.updateUser(userId, requestBody)
                    .map(this::toResponse)
                    .orElseThrow(() -> new NotFoundException("Could not find a user with id " + userId + "."));
        } catch (DuplicateKeyException e) {
            throw new ConflictException("An account with this email already exists: " + requestBody.getEmail());
        }
    }

    // Delete a user by id (404 if not found).
    @Transactional
    public void deleteUser(long userId){
        repository.fetchUserById(userId)
                .orElseThrow(() -> new NotFoundException("Could not find a user with id " + userId + "."));

        repository.removeUser(userId);
    }

    // List all users.
    public List<UserResponse> getAllUsers(){
        return repository.fetchAllUsers().stream()
                .map(this::toResponse)
                .toList();
    }

    // Get one user by id (404 if not found).
    public UserResponse getUserById(long userId){
        return repository.fetchUserById(userId)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Could not find a user with id " + userId + "."));
    }

    // Map the persistence model to the API response shape.
    private UserResponse toResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}

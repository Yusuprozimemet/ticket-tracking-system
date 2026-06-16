package net.hackyourfuture.tickettrackingsystem.controller;

import jakarta.validation.Valid;
import net.hackyourfuture.tickettrackingsystem.dto.requests.UserRequest;
import net.hackyourfuture.tickettrackingsystem.dto.responses.UserResponse;
import net.hackyourfuture.tickettrackingsystem.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service){
        this.service = service;
    }

    // POST /api/users - create a new user.
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody UserRequest requestBody){
        return service.createUser(requestBody);
    }

    // PUT /api/users/{id} - update an existing user.
     @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateUser(@PathVariable long id, @Valid @RequestBody UserRequest requestBody){
        return service.updateUser(id, requestBody);
    }

    // DELETE /api/users/{id} - delete a user.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long id){
        service.deleteUser(id);
    }

    // GET /api/users - list all users.
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getAllUsers(){
        return service.getAllUsers();
    }

    // GET /api/users/{id} - get one user by id.
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserById(@PathVariable long id){
        return service.getUserById(id);
    }



}

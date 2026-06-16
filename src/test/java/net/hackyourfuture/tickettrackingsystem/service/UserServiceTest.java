package net.hackyourfuture.tickettrackingsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import net.hackyourfuture.tickettrackingsystem.dto.requests.UserRequest;
import net.hackyourfuture.tickettrackingsystem.dto.responses.UserResponse;
import net.hackyourfuture.tickettrackingsystem.exception.DuplicateEmailException;
import net.hackyourfuture.tickettrackingsystem.exception.UserNotFoundException;
import net.hackyourfuture.tickettrackingsystem.model.User;
import net.hackyourfuture.tickettrackingsystem.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserById_returnsUser_whenFound() {
        // Arrange
        User user = User.builder().id(1L).name("Yusup").email("yusup@example.com").build();
        when(userRepository.fetchUserById(1L)).thenReturn(Optional.of(user));

        // Act
        UserResponse result = userService.getUserById(1L);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getUserById_throwsNotFound_whenMissing() {
        // Arrange
        when(userRepository.fetchUserById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void createUser_throwsDuplicateEmail_whenEmailTaken() {
        // Arrange: the repository rejects the insert because the email is not unique
        UserRequest request = new UserRequest("Tester", "tester@example.com");
        when(userRepository.createUser(any(UserRequest.class)))
                .thenThrow(new DuplicateKeyException("duplicate key value violates unique constraint"));

        // Act + Assert: the service translates it into a domain-specific exception
        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(DuplicateEmailException.class);
    }
}
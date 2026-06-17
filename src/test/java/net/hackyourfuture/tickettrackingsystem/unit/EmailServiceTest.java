package net.hackyourfuture.tickettrackingsystem.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.hackyourfuture.tickettrackingsystem.client.ResendClient;
import net.hackyourfuture.tickettrackingsystem.client.ResendClient.ResendEmailRequest;
import net.hackyourfuture.tickettrackingsystem.dto.responses.TicketResponse;
import net.hackyourfuture.tickettrackingsystem.dto.responses.UserResponse;
import net.hackyourfuture.tickettrackingsystem.model.Status;
import net.hackyourfuture.tickettrackingsystem.service.EmailService;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock private ResendClient resendClient;

    @Test
    void sendTicketChanged_sendsEmailToRecipient() {
        // Arrange
        EmailService emailService = new EmailService(resendClient, "noreply@test.com");
        TicketResponse ticket = TicketResponse.builder().id(1L).title("Bug").status(Status.OPEN).build();
        List<UserResponse> recipients = List.of(
                UserResponse.builder().id(1L).name("Yusup").email("yusup@example.com").build());

        // Act
        emailService.sendTicketChanged(ticket, "updated", recipients);

        // Assert
        verify(resendClient).sendEmail(any(ResendEmailRequest.class));
    }
}

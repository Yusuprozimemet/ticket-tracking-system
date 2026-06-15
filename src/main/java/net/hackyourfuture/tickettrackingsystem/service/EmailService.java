package net.hackyourfuture.tickettrackingsystem.service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import net.hackyourfuture.tickettrackingsystem.client.ResendClient;
import net.hackyourfuture.tickettrackingsystem.client.ResendClient.ResendEmailRequest;
import net.hackyourfuture.tickettrackingsystem.dto.responses.TicketResponse;
import net.hackyourfuture.tickettrackingsystem.dto.responses.UserResponse;

@Service
public class EmailService {

    private final ResendClient resendClient;
    private final String from;

    public EmailService(ResendClient resendClient,
                        @Value("${resend.from:onboarding@resend.dev}") String from) {
        this.resendClient = resendClient;
        this.from = from;
    }

    public void sendTicketChanged(TicketResponse ticket, String action, List<UserResponse> recipients) {
        String subject = "Ticket #" + ticket.getId() + " " + action + ": " + ticket.getTitle();
        String html = buildHtml(ticket, action, recipients);

        for (UserResponse recipient : recipients) {
            resendClient.sendEmail(
                    new ResendEmailRequest(from, List.of(recipient.getEmail()), subject, html));
        }
    }

    private String buildHtml(TicketResponse ticket, String action, List<UserResponse> recipients) {
        String assigneeNames = recipients.stream()
                .map(UserResponse::getName)
                .collect(Collectors.joining(", "));

        String template = loadTemplate();
        return template
                .replace("{{ticketId}}", String.valueOf(ticket.getId()))
                .replace("{{action}}", action)
                .replace("{{title}}", ticket.getTitle())
                .replace("{{status}}", String.valueOf(ticket.getStatus()))
                .replace("{{assignees}}", assigneeNames);
    }

    private String loadTemplate() {
        try {
            return StreamUtils.copyToString(
                    new ClassPathResource("templates/email.html").getInputStream(),
                    StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Could not load email template", e);
        }
    }
}

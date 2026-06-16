package net.hackyourfuture.tickettrackingsystem.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ResendClient {

    private final RestClient resendRestClient;

    public ResendClient(@Qualifier("resendRestClient") RestClient resendRestClient) {
        this.resendRestClient = resendRestClient;
    }

    // Send one email by calling Resend's POST /emails endpoint.
    public void sendEmail(ResendEmailRequest request) {
        resendRestClient
                .post()
                .uri("/emails")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    // Matches Resend's POST /emails request body.
    public record ResendEmailRequest(String from, List<String> to, String subject, String html) {
    }
}

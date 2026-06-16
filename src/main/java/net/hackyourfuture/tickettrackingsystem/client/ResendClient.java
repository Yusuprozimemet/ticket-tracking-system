package net.hackyourfuture.tickettrackingsystem.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import net.hackyourfuture.tickettrackingsystem.exception.ExternalApiException;

@Service
public class ResendClient {

    private final RestClient resendRestClient;

    public ResendClient(@Qualifier("resendRestClient") RestClient resendRestClient) {
        this.resendRestClient = resendRestClient;
    }

    // Send one email by calling Resend's POST /emails endpoint.
    public void sendEmail(ResendEmailRequest request) {
        try {
            resendRestClient
                    .post()
                    .uri("/emails")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, response) -> {
                        throw new ExternalApiException(
                                "Resend email provider error: " + response.getStatusCode());
                    })
                    .toBodilessEntity();
        } catch (RestClientException e) {
            // No HTTP response: connection refused, DNS failure, connect/read timeout, etc.
            throw new ExternalApiException(
                    "Resend could not be reached or returned an unexpected response.", e);
        }
    }

    // Matches Resend's POST /emails request body.
    public record ResendEmailRequest(String from, List<String> to, String subject, String html) {
    }
}

package net.hackyourfuture.tickettrackingsystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    // Build the HTTP client used to talk to Resend, with the API key attached.
    @Bean
    public RestClient resendRestClient(@Value("${resend.api-key:}") String apiKey) {
        return RestClient.builder()
                .baseUrl("https://api.resend.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
    }
    
}

package net.hackyourfuture.tickettrackingsystem.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    // Build the HTTP client used to talk to Resend, with the API key attached.
    // Timeouts keep a slow/unreachable Resend from tying up the request thread.
    @Bean
    public RestClient resendRestClient(@Value("${resend.api-key:}") String apiKey) {
        var requestFactory = ClientHttpRequestFactoryBuilder.detect().build(
                HttpClientSettings.defaults()
                        .withConnectTimeout(Duration.ofSeconds(2))
                        .withReadTimeout(Duration.ofSeconds(5)));

        return RestClient.builder()
                .baseUrl("https://api.resend.com")
                .requestFactory(requestFactory)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
    }

}

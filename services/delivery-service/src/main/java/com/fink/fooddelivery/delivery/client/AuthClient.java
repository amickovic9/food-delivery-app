package com.fink.fooddelivery.delivery.client;

import com.fink.fooddelivery.shared.contract.UserSummary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class AuthClient {

    private final WebClient http;

    public AuthClient(WebClient.Builder webClientBuilder,
                      @Value("${clients.auth.url:http://localhost:8081}") String baseUrl) {
        this.http = webClientBuilder.baseUrl(baseUrl).build();
    }

    public List<UserSummary> couriers() {
        return http.get().uri("/internal/couriers").retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserSummary>>() {
                })
                .block();
    }
}

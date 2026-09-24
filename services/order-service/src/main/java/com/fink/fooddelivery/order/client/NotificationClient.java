package com.fink.fooddelivery.order.client;

import com.fink.fooddelivery.shared.contract.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class NotificationClient {

    private final WebClient http;

    public NotificationClient(WebClient.Builder webClientBuilder,
                              @Value("${clients.notification.url:http://localhost:8084}") String baseUrl) {
        this.http = webClientBuilder.baseUrl(baseUrl).build();
    }

    public void notify(Long userId, String type, String message) {
        try {
            http.post().uri("/notifications")
                    .bodyValue(new NotificationRequest(userId, type, message))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (RuntimeException e) {
            log.warn("Notification for user {} failed: {}", userId, e.getMessage());
        }
    }
}

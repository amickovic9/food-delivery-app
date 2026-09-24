package com.fink.fooddelivery.delivery.client;

import com.fink.fooddelivery.shared.contract.UpdateOrderStatusRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class OrderClient {

    private final WebClient http;

    public OrderClient(WebClient.Builder webClientBuilder,
                       @Value("${clients.order.url:http://localhost:8086}") String baseUrl) {
        this.http = webClientBuilder.baseUrl(baseUrl).build();
    }

    public void updateStatus(Long orderId, String status) {
        try {
            http.put().uri("/orders/{id}/status", orderId)
                    .bodyValue(new UpdateOrderStatusRequest(status))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (RuntimeException e) {
            log.warn("Could not update order {} status to {}: {}", orderId, status, e.getMessage());
        }
    }
}

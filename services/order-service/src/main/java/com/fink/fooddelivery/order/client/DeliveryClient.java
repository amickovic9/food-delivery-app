package com.fink.fooddelivery.order.client;

import com.fink.fooddelivery.shared.contract.CreateDeliveryRequest;
import com.fink.fooddelivery.shared.contract.DeliveryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class DeliveryClient {

    private final WebClient http;

    public DeliveryClient(WebClient.Builder webClientBuilder,
                          @Value("${clients.delivery.url:http://localhost:8085}") String baseUrl) {
        this.http = webClientBuilder.baseUrl(baseUrl).build();
    }

    public DeliveryResponse createDelivery(Long orderId) {
        try {
            return http.post().uri("/deliveries")
                    .bodyValue(new CreateDeliveryRequest(orderId))
                    .retrieve()
                    .bodyToMono(DeliveryResponse.class)
                    .block();
        } catch (RuntimeException e) {
            log.warn("Delivery creation failed for order {}: {}", orderId, e.getMessage());
            return null;
        }
    }
}

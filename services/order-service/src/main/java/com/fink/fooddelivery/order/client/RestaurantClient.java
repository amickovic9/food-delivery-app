package com.fink.fooddelivery.order.client;

import com.fink.fooddelivery.shared.contract.RestaurantResponse;
import com.fink.fooddelivery.shared.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class RestaurantClient {

    private final WebClient http;

    public RestaurantClient(WebClient.Builder webClientBuilder,
                            @Value("${clients.restaurant.url:http://localhost:8082}") String baseUrl) {
        this.http = webClientBuilder.baseUrl(baseUrl).build();
    }

    public RestaurantResponse getRestaurant(Long id) {
        try {
            return http.get().uri("/restaurants/{id}", id).retrieve()
                    .bodyToMono(RestaurantResponse.class).block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new NotFoundException("Restaurant " + id + " not found");
            }
            throw e;
        }
    }
}

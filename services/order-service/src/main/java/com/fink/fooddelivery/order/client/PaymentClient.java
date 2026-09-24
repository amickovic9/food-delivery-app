package com.fink.fooddelivery.order.client;

import com.fink.fooddelivery.shared.contract.ChargeRequest;
import com.fink.fooddelivery.shared.contract.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class PaymentClient {

    private final WebClient http;

    public PaymentClient(WebClient.Builder webClientBuilder,
                         @Value("${clients.payment.url:http://localhost:8083}") String baseUrl) {
        this.http = webClientBuilder.baseUrl(baseUrl).build();
    }

    public PaymentResponse charge(ChargeRequest request) {
        try {
            return http.post().uri("/payments").bodyValue(request).retrieve()
                    .bodyToMono(PaymentResponse.class).block();
        } catch (RuntimeException e) {
            log.warn("Payment call failed for order {}: {}", request.orderId(), e.getMessage());
            return new PaymentResponse(null, "FAILED");
        }
    }
}

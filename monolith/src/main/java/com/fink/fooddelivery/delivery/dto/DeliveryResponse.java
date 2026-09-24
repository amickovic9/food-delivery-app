package com.fink.fooddelivery.delivery.dto;

import java.time.Instant;

public record DeliveryResponse(
        Long id,
        Long orderId,
        Long courierId,
        String status,
        Instant createdAt,
        Instant assignedAt,
        Instant pickedUpAt,
        Instant deliveredAt
) {
}

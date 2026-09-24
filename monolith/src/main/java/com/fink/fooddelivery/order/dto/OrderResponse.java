package com.fink.fooddelivery.order.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        Long restaurantId,
        String restaurantName,
        String status,
        String address,
        String note,
        BigDecimal totalPrice,
        List<OrderLineResponse> items,
        Instant createdAt
) {
}

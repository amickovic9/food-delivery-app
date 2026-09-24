package com.fink.fooddelivery.shared.contract;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        Long userId,
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

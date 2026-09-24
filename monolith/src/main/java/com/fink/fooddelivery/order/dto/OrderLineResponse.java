package com.fink.fooddelivery.order.dto;

import java.math.BigDecimal;

public record OrderLineResponse(
        Long menuItemId,
        String name,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal
) {
}

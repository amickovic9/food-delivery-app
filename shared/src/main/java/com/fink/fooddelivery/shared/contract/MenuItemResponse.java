package com.fink.fooddelivery.shared.contract;

import java.math.BigDecimal;

public record MenuItemResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        boolean available
) {
}

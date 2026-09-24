package com.fink.fooddelivery.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RequestItems(
        @NotNull Long menuItemId,
        @Positive int quantity
) {
}

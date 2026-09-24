package com.fink.fooddelivery.shared.contract;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RequestItems(
        @NotNull Long menuItemId,
        @Positive int quantity
) {
}

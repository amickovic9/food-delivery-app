package com.fink.fooddelivery.shared.contract;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ChargeRequest(
        @NotNull Long orderId,
        @NotNull @Positive BigDecimal amount,
        String method
) {
}

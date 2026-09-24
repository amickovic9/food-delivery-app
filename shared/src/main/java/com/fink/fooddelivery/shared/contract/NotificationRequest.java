package com.fink.fooddelivery.shared.contract;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificationRequest(
        @NotNull Long userId,
        @NotBlank String type,
        String message
) {
}

package com.fink.fooddelivery.shared.contract;

import jakarta.validation.constraints.NotNull;

public record CreateDeliveryRequest(@NotNull Long orderId) {
}

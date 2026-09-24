package com.fink.fooddelivery.shared.contract;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PlaceOrderRequest(
        @NotNull Long userId,
        @NotNull Long restaurantId,
        String address,
        String note,
        String paymentMethod,
        @NotEmpty @Valid List<RequestItems> items
) {
}

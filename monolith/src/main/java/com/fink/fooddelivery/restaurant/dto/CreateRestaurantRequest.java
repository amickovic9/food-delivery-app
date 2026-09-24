package com.fink.fooddelivery.restaurant.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateRestaurantRequest(
        @NotBlank String name,
        @NotBlank String address,
        String description
) {
}

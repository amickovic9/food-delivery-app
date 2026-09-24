package com.fink.fooddelivery.restaurant.dto;

import java.util.List;

public record RestaurantResponse(
        Long id,
        String name,
        String address,
        String description,
        boolean active,
        List<MenuItemResponse> menu
) {
}

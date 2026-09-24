package com.fink.fooddelivery.shared.contract;

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

package com.fink.fooddelivery.restaurant;

import com.fink.fooddelivery.restaurant.dto.CreateMenuItemRequest;
import com.fink.fooddelivery.restaurant.dto.CreateRestaurantRequest;
import com.fink.fooddelivery.restaurant.dto.MenuItemResponse;
import com.fink.fooddelivery.restaurant.dto.RestaurantResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping
    public List<RestaurantResponse> list() {
        return restaurantService.listActive();
    }

    @GetMapping("/{id}")
    public RestaurantResponse get(@PathVariable Long id) {
        return restaurantService.getWithMenu(id);
    }

    @PostMapping
    public RestaurantResponse create(@Valid @RequestBody CreateRestaurantRequest request) {
        return restaurantService.create(request);
    }

    @PostMapping("/{id}/menu")
    public MenuItemResponse addMenuItem(@PathVariable Long id,
                                       @Valid @RequestBody CreateMenuItemRequest request) {
        return restaurantService.addMenuItem(id, request);
    }
}

package com.fink.fooddelivery.restaurant;

import com.fink.fooddelivery.restaurant.dto.CreateMenuItemRequest;
import com.fink.fooddelivery.restaurant.dto.CreateRestaurantRequest;
import com.fink.fooddelivery.shared.contract.MenuItemResponse;
import com.fink.fooddelivery.shared.contract.RestaurantResponse;
import com.fink.fooddelivery.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Transactional(readOnly = true)
    public List<RestaurantResponse> listActive() {
        return restaurantRepository.findByActiveTrue().stream()
                .map(r -> new RestaurantResponse(r.getId(), r.getName(), r.getAddress(),
                        r.getDescription(), r.isActive(), null))
                .toList();
    }

    @Transactional(readOnly = true)
    public RestaurantResponse getWithMenu(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Restaurant " + id + " not found"));
        List<MenuItemResponse> menu = restaurant.getMenu().stream()
                .map(this::toMenuItemResponse)
                .toList();
        return new RestaurantResponse(restaurant.getId(), restaurant.getName(), restaurant.getAddress(),
                restaurant.getDescription(), restaurant.isActive(), menu);
    }

    @Transactional
    public RestaurantResponse create(CreateRestaurantRequest request) {
        Restaurant saved = restaurantRepository.save(
                new Restaurant(request.name(), request.address(), request.description()));
        return new RestaurantResponse(saved.getId(), saved.getName(), saved.getAddress(),
                saved.getDescription(), saved.isActive(), null);
    }

    @Transactional
    public MenuItemResponse addMenuItem(Long restaurantId, CreateMenuItemRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant " + restaurantId + " not found"));
        MenuItem item = restaurant.addMenuItem(
                new MenuItem(request.name(), request.description(), request.price()));
        restaurantRepository.save(restaurant);
        return toMenuItemResponse(item);
    }

    private MenuItemResponse toMenuItemResponse(MenuItem item) {
        return new MenuItemResponse(item.getId(), item.getName(), item.getDescription(),
                item.getPrice(), item.isAvailable());
    }
}

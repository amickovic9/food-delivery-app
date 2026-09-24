package com.fink.fooddelivery.delivery;

import com.fink.fooddelivery.delivery.dto.DeliveryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping("/{id}")
    public DeliveryResponse get(@PathVariable Long id) {
        return deliveryService.getById(id);
    }

    @GetMapping
    public List<DeliveryResponse> forCourier(@RequestParam Long courierId) {
        return deliveryService.forCourier(courierId);
    }

    @PostMapping("/{id}/pickup")
    public DeliveryResponse pickup(@PathVariable Long id) {
        return deliveryService.markPickedUp(id);
    }

    @PostMapping("/{id}/deliver")
    public DeliveryResponse deliver(@PathVariable Long id) {
        return deliveryService.markDelivered(id);
    }
}

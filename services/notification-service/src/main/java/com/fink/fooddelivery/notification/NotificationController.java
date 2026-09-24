package com.fink.fooddelivery.notification;

import com.fink.fooddelivery.shared.contract.NotificationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public void notify(@Valid @RequestBody NotificationRequest request) {
        notificationService.record(request);
    }
}

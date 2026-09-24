package com.fink.fooddelivery.notification;

import com.fink.fooddelivery.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Value("${app.notification.latency-ms:5}")
    private long latencyMs;

    @Transactional
    public void notify(User user, NotificationType type, String message) {
        if (latencyMs > 0) {
            try {
                Thread.sleep(latencyMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        notificationRepository.save(new Notification(user, type, message));
    }
}

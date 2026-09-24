package com.fink.fooddelivery.notification;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String type;

    @Column(length = 500)
    private String message;

    @CreationTimestamp
    private Instant createdAt;

    public Notification(Long userId, String type, String message) {
        this.userId = userId;
        this.type = type;
        this.message = message;
    }
}

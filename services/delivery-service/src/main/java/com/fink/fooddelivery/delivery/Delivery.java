package com.fink.fooddelivery.delivery;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
@NoArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long orderId;

    private Long courierId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status = DeliveryStatus.PENDING;

    @CreationTimestamp
    private Instant createdAt;
    private Instant assignedAt;
    private Instant pickedUpAt;
    private Instant deliveredAt;

    public Delivery(Long orderId) {
        this.orderId = orderId;
    }

    public void assignTo(Long courierId) {
        this.courierId = courierId;
        this.status = DeliveryStatus.ASSIGNED;
        this.assignedAt = Instant.now();
    }

    public void markPickedUp() {
        this.status = DeliveryStatus.PICKED_UP;
        this.pickedUpAt = Instant.now();
    }

    public void markDelivered() {
        this.status = DeliveryStatus.DELIVERED;
        this.deliveredAt = Instant.now();
    }
}

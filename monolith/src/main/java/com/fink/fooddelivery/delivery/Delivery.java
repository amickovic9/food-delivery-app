package com.fink.fooddelivery.delivery;

import com.fink.fooddelivery.order.Order;
import com.fink.fooddelivery.user.User;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orderId", unique = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courierId")
    private User courier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status = DeliveryStatus.PENDING;

    @CreationTimestamp
    private Instant createdAt;
    private Instant assignedAt;
    private Instant pickedUpAt;
    private Instant deliveredAt;

    public Delivery(Order order) {
        this.order = order;
    }

    public void assignTo(User courier) {
        this.courier = courier;
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

package com.fink.fooddelivery.delivery;

import com.fink.fooddelivery.common.exception.NotFoundException;
import com.fink.fooddelivery.delivery.dto.DeliveryResponse;
import com.fink.fooddelivery.notification.NotificationService;
import com.fink.fooddelivery.notification.NotificationType;
import com.fink.fooddelivery.order.Order;
import com.fink.fooddelivery.order.OrderRepository;
import com.fink.fooddelivery.order.OrderStatus;
import com.fink.fooddelivery.user.Role;
import com.fink.fooddelivery.user.User;
import com.fink.fooddelivery.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Transactional
    public Delivery createByOrder(Order order) {
        Delivery delivery = new Delivery(order);
        List<User> couriers = userService.findByRole(Role.ROLE_COURIER);
        if (!couriers.isEmpty()) {
            User courier = couriers.get(ThreadLocalRandom.current().nextInt(couriers.size()));
            delivery.assignTo(courier);
        }
        return deliveryRepository.save(delivery);
    }

    @Transactional(readOnly = true)
    public DeliveryResponse getById(Long id) {
        return toResponse(loadDelivery(id));
    }

    @Transactional(readOnly = true)
    public List<DeliveryResponse> forCourier(Long courierId) {
        return deliveryRepository.findByCourierIdOrderByCreatedAtDesc(courierId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public DeliveryResponse markPickedUp(Long id) {
        Delivery delivery = loadDelivery(id);
        delivery.markPickedUp();
        delivery = deliveryRepository.save(delivery);
        Order order = delivery.getOrder();
        order.setStatus(OrderStatus.PICKED_UP);
        orderRepository.save(order);
        return toResponse(delivery);
    }

    @Transactional
    public DeliveryResponse markDelivered(Long id) {
        Delivery delivery = loadDelivery(id);
        delivery.markDelivered();
        delivery = deliveryRepository.save(delivery);
        Order order = delivery.getOrder();
        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
        notificationService.notify(order.getUser(), NotificationType.ORDER_DELIVERED,
                "Your order #" + order.getId() + " has been delivered.");
        return toResponse(delivery);
    }

    private Delivery loadDelivery(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Delivery " + id + " not found"));
    }

    private DeliveryResponse toResponse(Delivery d) {
        Long courierId = d.getCourier() == null ? null : d.getCourier().getId();
        return new DeliveryResponse(d.getId(), d.getOrder().getId(), courierId, d.getStatus().name(),
                d.getCreatedAt(), d.getAssignedAt(), d.getPickedUpAt(), d.getDeliveredAt());
    }
}

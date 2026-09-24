package com.fink.fooddelivery.delivery;

import com.fink.fooddelivery.delivery.client.AuthClient;
import com.fink.fooddelivery.delivery.client.OrderClient;
import com.fink.fooddelivery.shared.contract.CreateDeliveryRequest;
import com.fink.fooddelivery.shared.contract.DeliveryResponse;
import com.fink.fooddelivery.shared.contract.UserSummary;
import com.fink.fooddelivery.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final AuthClient authClient;
    private final OrderClient orderClient;

    @Transactional
    public DeliveryResponse createByOrder(CreateDeliveryRequest request) {
        Delivery delivery = new Delivery(request.orderId());
        List<UserSummary> couriers = authClient.couriers();
        if (couriers != null && !couriers.isEmpty()) {
            UserSummary courier = couriers.get(ThreadLocalRandom.current().nextInt(couriers.size()));
            delivery.assignTo(courier.id());
        }
        return toResponse(deliveryRepository.save(delivery));
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
        orderClient.updateStatus(delivery.getOrderId(), "PICKED_UP");
        return toResponse(delivery);
    }

    @Transactional
    public DeliveryResponse markDelivered(Long id) {
        Delivery delivery = loadDelivery(id);
        delivery.markDelivered();
        delivery = deliveryRepository.save(delivery);
        orderClient.updateStatus(delivery.getOrderId(), "DELIVERED");
        return toResponse(delivery);
    }

    private Delivery loadDelivery(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Delivery " + id + " not found"));
    }

    private DeliveryResponse toResponse(Delivery d) {
        return new DeliveryResponse(d.getId(), d.getOrderId(), d.getCourierId(), d.getStatus().name(),
                d.getCreatedAt(), d.getAssignedAt(), d.getPickedUpAt(), d.getDeliveredAt());
    }
}

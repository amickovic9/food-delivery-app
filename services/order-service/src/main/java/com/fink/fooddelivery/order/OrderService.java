package com.fink.fooddelivery.order;

import com.fink.fooddelivery.order.client.DeliveryClient;
import com.fink.fooddelivery.order.client.NotificationClient;
import com.fink.fooddelivery.order.client.PaymentClient;
import com.fink.fooddelivery.order.client.RestaurantClient;
import com.fink.fooddelivery.shared.contract.*;
import com.fink.fooddelivery.shared.exception.BadRequestException;
import com.fink.fooddelivery.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantClient restaurantClient;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;
    private final NotificationClient notificationClient;

    public OrderResponse createOrder(PlaceOrderRequest request) {
        RestaurantResponse restaurant = restaurantClient.getRestaurant(request.restaurantId());

        Order order = new Order(request.userId(), restaurant.id(), restaurant.name(),
                request.address(), request.note());
        for (RequestItems line : request.items()) {
            MenuItemResponse item = getOrderableItemById(restaurant, line.menuItemId());
            order.addItem(item.id(), item.name(), item.price(), line.quantity());
        }
        order.recalculateTotal();
        order = orderRepository.save(order);

        List<OrderLineResponse> lines = mapLines(order);

        String method = request.paymentMethod() == null ? "CARD" : request.paymentMethod();
        PaymentResponse payment = paymentClient.charge(
                new ChargeRequest(order.getId(), order.getTotalPrice(), method));

        if (!payment.isSuccess()) {
            order.setStatus(OrderStatus.PAYMENT_FAILED);
            order = orderRepository.save(order);
            notificationClient.notify(order.getUserId(), "PAYMENT_FAILED",
                    "Payment for order #" + order.getId() + " failed.");
            return toResponse(order, lines);
        }

        order.setStatus(OrderStatus.PAID);
        order = orderRepository.save(order);

        DeliveryResponse delivery = deliveryClient.createDelivery(order.getId());
        order.setStatus(delivery != null && delivery.courierId() != null
                ? OrderStatus.ASSIGNED : OrderStatus.PREPARING);
        order = orderRepository.save(order);

        notificationClient.notify(order.getUserId(), "ORDER_CONFIRMED",
                "Your order #" + order.getId() + " is confirmed.");
        return toResponse(order, lines);
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Long orderId) {
        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new NotFoundException("Order " + orderId + " not found"));
        return toResponse(order, mapLines(order));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listForUser(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(order -> toResponse(order, mapLines(order)))
                .toList();
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, String status) {
        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new NotFoundException("Order " + orderId + " not found"));
        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown order status: " + status);
        }
        order.setStatus(newStatus);
        order = orderRepository.save(order);
        if (newStatus == OrderStatus.DELIVERED) {
            notificationClient.notify(order.getUserId(), "ORDER_DELIVERED",
                    "Your order #" + order.getId() + " has been delivered.");
        }
        return toResponse(order, mapLines(order));
    }

    private MenuItemResponse getOrderableItemById(RestaurantResponse restaurant, Long menuItemId) {
        List<MenuItemResponse> menu = restaurant.menu() == null ? List.of() : restaurant.menu();
        return menu.stream()
                .filter(item -> item.id().equals(menuItemId))
                .findFirst()
                .filter(MenuItemResponse::available)
                .orElseThrow(() -> new BadRequestException(
                        "Menu item " + menuItemId + " is not available at restaurant " + restaurant.id()));
    }

    private List<OrderLineResponse> mapLines(Order order) {
        return order.getItems().stream()
                .map(item -> new OrderLineResponse(item.getMenuItemId(), item.getNameSnapshot(),
                        item.getUnitPrice(), item.getQuantity(), item.lineTotal()))
                .toList();
    }

    private OrderResponse toResponse(Order order, List<OrderLineResponse> lines) {
        return new OrderResponse(order.getId(), order.getUserId(), order.getRestaurantId(),
                order.getRestaurantName(), order.getStatus().name(), order.getAddress(), order.getNote(),
                order.getTotalPrice(), lines, order.getCreatedAt());
    }
}

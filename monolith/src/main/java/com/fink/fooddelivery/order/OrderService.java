package com.fink.fooddelivery.order;

import com.fink.fooddelivery.common.exception.NotFoundException;
import com.fink.fooddelivery.delivery.Delivery;
import com.fink.fooddelivery.delivery.DeliveryService;
import com.fink.fooddelivery.notification.NotificationService;
import com.fink.fooddelivery.notification.NotificationType;
import com.fink.fooddelivery.order.dto.OrderLineResponse;
import com.fink.fooddelivery.order.dto.RequestItems;
import com.fink.fooddelivery.order.dto.OrderResponse;
import com.fink.fooddelivery.order.dto.PlaceOrderRequest;
import com.fink.fooddelivery.payment.PaymentResult;
import com.fink.fooddelivery.payment.PaymentService;
import com.fink.fooddelivery.restaurant.MenuItem;
import com.fink.fooddelivery.restaurant.Restaurant;
import com.fink.fooddelivery.restaurant.RestaurantService;
import com.fink.fooddelivery.user.User;
import com.fink.fooddelivery.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final RestaurantService restaurantService;
    private final PaymentService paymentService;
    private final DeliveryService deliveryService;
    private final NotificationService notificationService;

    @Transactional
    public OrderResponse createOrder(PlaceOrderRequest request) {
        User user = userService.getById(request.userId());
        Restaurant restaurant = restaurantService.getById(request.restaurantId());

        Order order = new Order(user, restaurant, request.address(), request.note());
        for (RequestItems requestItem : request.items()) {
            MenuItem item = restaurantService.getOrderableItemById(restaurant.getId(), requestItem.menuItemId());
            order.addItem(item, requestItem.quantity());
        }
        order.recalculateTotal();
        order = orderRepository.save(order);

        String method = request.paymentMethod() == null ? "CARD" : request.paymentMethod();
        PaymentResult payment = paymentService.charge(order, method);

        if (!payment.isSuccess()) {
            order.setStatus(OrderStatus.PAYMENT_FAILED);
            order = orderRepository.save(order);
            notificationService.notify(user, NotificationType.PAYMENT_FAILED,
                    "Payment for order #" + order.getId() + " failed.");
            return toResponse(order);
        }

        order.setStatus(OrderStatus.PAID);
        order = orderRepository.save(order);

        Delivery delivery = deliveryService.createByOrder(order);
        order.setStatus(delivery.getCourier() != null ? OrderStatus.ASSIGNED : OrderStatus.PREPARING);
        order = orderRepository.save(order);

        notificationService.notify(user, NotificationType.ORDER_CONFIRMED,
                "Your order #" + order.getId() + " is confirmed.");
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Long orderId) {
        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new NotFoundException("Order " + orderId + " not found"));
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listForUser(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private OrderResponse toResponse(Order order) {
        List<OrderLineResponse> lines = order.getItems().stream()
                .map(item -> new OrderLineResponse(item.getMenuItem().getId(), item.getNameSnapshot(),
                        item.getUnitPrice(), item.getQuantity(), item.lineTotal()))
                .toList();
        return new OrderResponse(order.getId(), order.getRestaurant().getId(), order.getRestaurantName(),
                order.getStatus().name(), order.getAddress(), order.getNote(),
                order.getTotalPrice(), lines, order.getCreatedAt());
    }
}

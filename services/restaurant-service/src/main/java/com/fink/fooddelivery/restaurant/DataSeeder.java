package com.fink.fooddelivery.restaurant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private final RestaurantRepository restaurantRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (restaurantRepository.count() > 0) {
            return;
        }
        restaurantRepository.saveAll(sampleRestaurants());
        log.info("Seed complete: {} restaurants", restaurantRepository.count());
    }

    private List<Restaurant> sampleRestaurants() {
        String[][] menus = {
                {"Margherita:7.90", "Capricciosa:9.50", "Diavola:9.90", "Quattro Formaggi:10.50",
                        "Calzone:9.20", "Garlic Bread:3.50", "Tiramisu:4.80", "Cola 0.5:2.20"},
                {"Cheeseburger:6.50", "Double Bacon:9.90", "Veggie Burger:6.90", "Chicken Burger:7.20",
                        "Fries:2.90", "Onion Rings:3.40", "Milkshake:4.10", "Water 0.5:1.50"},
                {"California Roll:8.90", "Salmon Nigiri:6.50", "Tuna Sashimi:9.80", "Veg Maki:5.90",
                        "Miso Soup:3.20", "Edamame:3.80", "Mochi:4.20", "Green Tea:2.00"},
                {"Carbonara:8.40", "Bolognese:8.20", "Pesto Genovese:7.90", "Lasagna:9.60",
                        "Bruschetta:4.50", "Caprese:5.80", "Panna Cotta:4.60", "Espresso:1.80"},
                {"Pad Thai:8.10", "Green Curry:8.90", "Tom Yum:7.60", "Spring Rolls:4.20",
                        "Fried Rice:6.90", "Satay Skewers:5.50", "Sticky Rice Mango:4.90", "Thai Iced Tea:3.10"},
                {"Chicken Burrito:7.80", "Beef Tacos (3):7.20", "Quesadilla:6.60", "Nachos:5.90",
                        "Guacamole:3.60", "Churros:4.10", "Horchata:2.80", "Jarritos:2.40"},
        };
        String[] names = {"Napoli Pizza", "Big Bite Burgers", "Sakura Sushi", "Trattoria Roma",
                "Bangkok Street", "El Toro Cantina"};
        String[] addresses = {"Knez Mihailova 1", "Bulevar 42", "Cara Dušana 15", "Njegoševa 7",
                "Takovska 23", "Vojvode Stepe 88"};

        List<Restaurant> restaurants = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            Restaurant r = new Restaurant(names[i], addresses[i], names[i] + " — authentic and fast");
            for (String entry : menus[i]) {
                String[] parts = entry.split(":");
                r.addMenuItem(new MenuItem(parts[0], parts[0], new BigDecimal(parts[1])));
            }
            restaurants.add(r);
        }
        return restaurants;
    }
}

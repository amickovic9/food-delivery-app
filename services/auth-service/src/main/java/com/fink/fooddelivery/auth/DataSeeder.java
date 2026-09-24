package com.fink.fooddelivery.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private static final String DEFAULT_PASSWORD = "password";
    private static final int CUSTOMERS = 20;
    private static final int COURIERS = 5;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }
        String hash = passwordEncoder.encode(DEFAULT_PASSWORD);

        userRepository.save(user("admin@fink.dev", "Ada", "Admin", hash, Role.ROLE_ADMIN));
        for (int i = 1; i <= COURIERS; i++) {
            userRepository.save(user("courier" + i + "@fink.dev", "Courier", "No" + i, hash, Role.ROLE_COURIER));
        }
        for (int i = 1; i <= CUSTOMERS; i++) {
            userRepository.save(user("user" + i + "@fink.dev", "User", "No" + i, hash, Role.ROLE_USER));
        }
        log.info("Seed complete: {} users (default password '{}')", userRepository.count(), DEFAULT_PASSWORD);
    }

    private User user(String email, String first, String last, String passwordHash, Role role) {
        User u = new User();
        u.setEmail(email);
        u.setFirstName(first);
        u.setLastName(last);
        u.setPhoneNumber("+38160000" + Math.abs(email.hashCode() % 10000));
        u.setPassword(passwordHash);
        u.setRoles(new ArrayList<>(List.of(role)));
        return u;
    }
}

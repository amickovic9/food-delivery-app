package com.fink.fooddelivery.auth;

import com.fink.fooddelivery.shared.contract.UserSummary;
import com.fink.fooddelivery.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserRepository userRepository;

    @GetMapping("/couriers")
    @Transactional(readOnly = true)
    public List<UserSummary> couriers() {
        return userRepository.findByRolesContaining(Role.ROLE_COURIER).stream()
                .map(u -> new UserSummary(u.getId(), u.getEmail()))
                .toList();
    }

    @GetMapping("/users/{id}")
    @Transactional(readOnly = true)
    public UserSummary user(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(u -> new UserSummary(u.getId(), u.getEmail()))
                .orElseThrow(() -> new NotFoundException("User " + id + " not found"));
    }
}

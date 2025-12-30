package com.fink.fooddelivery.repository;

import com.fink.fooddelivery.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    User findByJwtTokens(String bearer);
}

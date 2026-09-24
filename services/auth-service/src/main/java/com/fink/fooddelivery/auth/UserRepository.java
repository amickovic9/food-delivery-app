package com.fink.fooddelivery.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRolesContaining(Role role);

    @Query("select u from User u join u.jwtTokens t where t = :token")
    Optional<User> findByActiveToken(@Param("token") String token);
}

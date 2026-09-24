package com.fink.fooddelivery.user;

import com.fink.fooddelivery.user.dto.RegisterUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Setter
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "userId"))
    @Column(name = "role")
    private List<Role> roles = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_tokens", joinColumns = @JoinColumn(name = "userId"))
    @Column(name = "jwtToken", length = 512)
    private List<String> jwtTokens = new ArrayList<>();

    public User(RegisterUser registerUser) {
        this.email = registerUser.email();
        this.firstName = registerUser.firstName();
        this.lastName = registerUser.lastName();
        this.phoneNumber = registerUser.phoneNumber();
        this.roles = new ArrayList<>(List.of(Role.ROLE_USER));
    }
}

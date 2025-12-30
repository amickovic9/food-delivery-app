package com.fink.fooddelivery.domain;

import com.fink.fooddelivery.dto.auth.RegisterUser;
import com.fink.fooddelivery.enums.Role;
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
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;
    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "userId")
    )
    @Column(name = "role")
    private List<Role> roles;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "user_tokens",
            joinColumns = @JoinColumn(name = "userId")
    )
    @Column(name = "jwtToken")
    private List<String> jwtTokens;

    public User(RegisterUser registerUser){
        List<Role> roles = new ArrayList<>();
        roles.add(Role.ROLE_USER);
        this.email = registerUser.getEmail();
        this.firstName = registerUser.getFirstName();
        this.lastName = registerUser.getLastName();
        this.phoneNumber = registerUser.getPhoneNumber();
        this.roles = roles;
    }
}

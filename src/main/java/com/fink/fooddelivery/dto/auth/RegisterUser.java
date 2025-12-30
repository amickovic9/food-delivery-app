package com.fink.fooddelivery.dto.auth;

import lombok.Getter;

@Getter
public class RegisterUser {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;
}

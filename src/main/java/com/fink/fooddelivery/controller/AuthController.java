package com.fink.fooddelivery.controller;

import com.fink.fooddelivery.config.JwtUtil;
import com.fink.fooddelivery.domain.User;
import com.fink.fooddelivery.dto.auth.LoginRequest;
import com.fink.fooddelivery.dto.auth.LoginResponse;
import com.fink.fooddelivery.dto.auth.RegisterUser;
import com.fink.fooddelivery.exception.AuthException;
import com.fink.fooddelivery.exception.UserAlreadyExistsException;
import com.fink.fooddelivery.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class AuthController {

    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public void registerUser(@RequestBody RegisterUser registerUser){
        if(userService.existsByEmail(registerUser.getEmail()))
            throw new UserAlreadyExistsException("There is already user with that email address");
        User user = new User(registerUser);
        user.setPassword(passwordEncoder.encode(registerUser.getPassword()));
        userService.save(user);
    }

    @PostMapping
    public LoginResponse login(@RequestBody LoginRequest loginRequest){
        User user = userService.findByEmail(loginRequest.getEmail());
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
            throw new AuthException("Wrong password");

        JwtUtil jwtUtil = new JwtUtil();
        String token = jwtUtil.generateToken(user.getEmail(), user.getRoles());
        user.getJwtTokens().add(token);
        userService.save(user);
        return new LoginResponse(token);
    }

    @PostMapping("/logout")
    public void logoutUser(@RequestHeader("Authorization") String bearer){
        bearer = bearer.substring(7);
        User user = userService.findByJwtToken(bearer);
        if (user == null)
            throw new AuthException("Unauthorized!");
        user.getJwtTokens().remove(bearer);
        userService.save(user);
    }
}

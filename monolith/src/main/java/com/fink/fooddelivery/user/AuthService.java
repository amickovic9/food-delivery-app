package com.fink.fooddelivery.user;

import com.fink.fooddelivery.common.config.JwtUtil;
import com.fink.fooddelivery.common.exception.AuthException;
import com.fink.fooddelivery.common.exception.UserAlreadyExistsException;
import com.fink.fooddelivery.user.dto.LoginRequest;
import com.fink.fooddelivery.user.dto.LoginResponse;
import com.fink.fooddelivery.user.dto.RegisterUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public void register(RegisterUser request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("There is already a user with that email address");
        }
        User user = new User(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthException("Invalid email or password"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AuthException("Invalid email or password");
        }
        String token = jwtUtil.generateToken(user.getEmail(), user.getRoles());
        user.getJwtTokens().add(token);
        userRepository.save(user);
        return new LoginResponse(token);
    }

    @Transactional
    public void logout(String token) {
        User user = userRepository.findByActiveToken(token)
                .orElseThrow(() -> new AuthException("Unauthorized!"));
        user.getJwtTokens().remove(token);
        userRepository.save(user);
    }
}

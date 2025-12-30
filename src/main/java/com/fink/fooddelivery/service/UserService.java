package com.fink.fooddelivery.service;

import com.fink.fooddelivery.domain.User;
import com.fink.fooddelivery.exception.AuthException;
import com.fink.fooddelivery.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class UserService {
    
    private UserRepository userRepository;
    
    @Transactional
    public void save(User user){
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("There is no user with that email!"));
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User findByJwtToken(String bearer) {
        return userRepository.findByJwtTokens(bearer);
    }
}

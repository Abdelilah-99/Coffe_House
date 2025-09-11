package com.blog.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.blog.dto.RegisterRequest;
import com.blog.dto.RegisterResponse;
import com.blog.entity.User;
import com.blog.repository.UserRepository;

@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponse register(RegisterRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            return new RegisterResponse("Registration failed: email already exist", null, null);
        }
        if (userRepository.findByUserName(req.getUsername()).isPresent()) {
            return new RegisterResponse("Registration failed: username already exist", null, null);
        }
        String hashedPassword = passwordEncoder.encode(req.getPassword());
        User newUser = new User(
                req.getEmail(),
                req.getFirstName(),
                req.getLastName(),
                req.getUsername(),
                req.getRole(),
                hashedPassword);
        try {
            userRepository.save(newUser);
            return new RegisterResponse("User registered successfully", req.getRole(), req.getUsername());
        } catch (Exception e) {
            return new RegisterResponse("Registration failed: " + e.getMessage(), null, null);
        }
    }
}

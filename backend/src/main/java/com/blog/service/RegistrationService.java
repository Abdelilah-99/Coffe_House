package com.blog.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.blog.dto.RegisterRequest;
import com.blog.dto.RegisterResponse;
import com.blog.entity.User;
import com.blog.exceptions.*;
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
            throw new UserAlreadyExistException(String.format("Email already Exists %s", req.getEmail()));
        }
        if (userRepository.findByUserName(req.getUsername()).isPresent()) {
            throw new UserAlreadyExistException(String.format("Username already Exists %s", req.getUsername()));
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
            throw new ErrSavingException(String.format("failed to save data %s", e));
        }
    }
}

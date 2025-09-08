package com.blog.service;

import com.blog.entity.User;

import org.springframework.stereotype.Service;

import com.blog.dto.AuthRequest;
import com.blog.dto.AuthResponse;
import com.blog.exceptions.InvalidPasswordException;
import com.blog.exceptions.UserNotFoundException;
import com.blog.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;

    AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResponse login(AuthRequest req) {
        User user = userRepository.findByUserName(req.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!user.getPassword().equals(req.getPassword())) {
            throw new InvalidPasswordException("Invalid pasword");
        }
        return new AuthResponse("Login successful", user.getRole(), user.getUserName());
    }
}

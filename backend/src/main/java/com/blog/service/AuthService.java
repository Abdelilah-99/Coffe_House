package com.blog.service;

import com.blog.entity.User;
import com.blog.dto.AuthRequest;
import com.blog.dto.AuthResponse;
import com.blog.exceptions.InvalidPasswordException;
import com.blog.exceptions.UserNotFoundException;
import com.blog.repository.UserRepository;
import com.blog.config.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(CustomUserDetailsService userDetailsService, UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils) {
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public AuthResponse login(AuthRequest req) {
        User user = userRepository.findByUserName(req.getUsername())
                .or(() -> userRepository.findByEmail(req.getEmail()))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Invalid password");
        }

        String token = jwtUtils.generateToken(user.getUserName(), user.getRole());
        return new AuthResponse("Login successful", user.getRole(), user.getUserName(), token);
    }
}

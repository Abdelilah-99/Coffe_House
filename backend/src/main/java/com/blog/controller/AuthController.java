package com.blog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.blog.config.JwtUtils;
import com.blog.dto.AuthRequest;
import com.blog.dto.AuthResponse;
import com.blog.dto.RegisterRequest;
import com.blog.dto.RegisterResponse;
import com.blog.exceptions.InvalidPasswordException;
import com.blog.exceptions.UserNotFoundException;
import com.blog.service.CustomUserDetailsService;
import com.blog.service.RegistrationService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final RegistrationService registerService;

    public AuthController(
            CustomUserDetailsService userDetailsService,
            JwtUtils jwtUtils,
            RegistrationService registerService) {
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.registerService = registerService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(req.getUsername());
            String token = jwtUtils.generateToken(userDetails);
            AuthResponse res = new AuthResponse(
                    "Login successful",
                    userDetails.getAuthorities().toString(),
                    userDetails.getUsername(),
                    token);
            return ResponseEntity.ok(res);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).build();
        } catch (InvalidPasswordException e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest req) {
        RegisterResponse res = registerService.register(req);

        if (res.getMessage().contains("successfully")) {
            return ResponseEntity.ok(res);
        } else if (res.getMessage().contains("already exist")) {
            return ResponseEntity.badRequest().body(res);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // @GetMapping("/me")

    // @PostMapping("/logout")
}
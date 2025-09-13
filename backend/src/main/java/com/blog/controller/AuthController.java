package com.blog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.blog.dto.AuthRequest;
import com.blog.dto.AuthResponse;
import com.blog.dto.RegisterRequest;
import com.blog.dto.RegisterResponse;
import com.blog.dto.UsersRespons;
import com.blog.exceptions.InvalidPasswordException;
import com.blog.exceptions.UserNotFoundException;
import com.blog.service.AuthService;
import com.blog.service.RegistrationService;
import com.blog.service.UsersServices;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final RegistrationService registerService;
    private final AuthService authService;
    private final UsersServices userService;

    public AuthController(
            UsersServices userService,
            AuthService authService,
            RegistrationService registerService) {
        this.userService = userService;
        this.registerService = registerService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        try {
            AuthResponse res = authService.login(req);
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

    @GetMapping("/me")
    public ResponseEntity<UsersRespons> getUserByUsername() {
        try {
            UsersRespons user = userService.getCurrentUser();
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            System.err.println("User not found12");
            return ResponseEntity.status(401).body(null);
        }
    }

    // @PostMapping("/logout")
}
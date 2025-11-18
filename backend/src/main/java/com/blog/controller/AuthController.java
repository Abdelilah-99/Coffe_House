package com.blog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.blog.dto.AuthRequest;
import com.blog.dto.AuthResponse;
import com.blog.dto.RegisterRequest;
import com.blog.dto.RegisterResponse;
import com.blog.dto.UsersRespons;
import com.blog.dto.UpdateProfileRequest;
import com.blog.dto.UpdateProfileResponse;
import com.blog.service.AuthService;
import com.blog.service.RegistrationService;
import com.blog.service.UsersServices;
import org.springframework.web.multipart.MultipartFile;
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
        AuthResponse res = authService.login(req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestPart("user") @Valid RegisterRequest req,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        RegisterResponse res = registerService.register(req, profileImage);
        return ResponseEntity.ok(res);
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

    @PutMapping("/me")
    public ResponseEntity<UpdateProfileResponse> updateProfile(
            @RequestPart("user") @Valid UpdateProfileRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        try {
            UpdateProfileResponse response = userService.updateProfile(request, profileImage);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error updating profile: " + e.getMessage());
            return ResponseEntity.status(400).body(null);
        }
    }
}
package com.blog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.blog.dto.AuthRequest;
import com.blog.dto.AuthResponse;
import com.blog.dto.RegisterRequest;
import com.blog.dto.RegisterResponse;
import com.blog.exceptions.InvalidPasswordException;
import com.blog.exceptions.UserNotFoundException;
import com.blog.service.AuthService;
import com.blog.service.RegistrationService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final RegistrationService registerService;

    public AuthController(AuthService authService, RegistrationService registerService) {
        this.authService = authService;
        this.registerService = registerService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        // boolean success =
        try {
            AuthResponse res = authService.login(req);
            // return "login end point";
            // System.out.println(req.getUsername());
            // System.out.println(req.getPassword());
            System.out.println(res.getUserName());
            return ResponseEntity.ok(res);
        } catch (UserNotFoundException e) {
            // TODO: handle exception
            System.out.println(e);
            return ResponseEntity.status(404).build();
        } catch (InvalidPasswordException e) {
            System.out.println(e);
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest req) {
        // return "register end point";
        System.out.printf("======================" + req.getUsername() + "=================\n");
        RegisterResponse res = registerService.register(req);
        if (res.getMessage().contains("successfully")) {
            return ResponseEntity.ok(res);
        } else if (res.getMessage().contains("already exist")) {
            return ResponseEntity.badRequest().body(res);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        // System.out.println(req.getUsername());
        // System.out.println(req.getPassword());
        // System.out.println(req.getEmail());
    }
}

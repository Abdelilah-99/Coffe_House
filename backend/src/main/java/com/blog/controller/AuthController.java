package com.blog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.blog.dto.AuthRequest;
import com.blog.dto.AuthResponse;
import com.blog.dto.RegisterRequest;
import com.blog.exceptions.InvalidPasswordException;
import com.blog.exceptions.UserNotFoundException;
import com.blog.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
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
    public void register(@RequestBody RegisterRequest req) {
        // return "register end point";
        System.out.println(req.getUsername());
        System.out.println(req.getPassword());
        System.out.println(req.getEmail());
    }
}

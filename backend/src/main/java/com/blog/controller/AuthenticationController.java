package com.blog.controller;

import org.springframework.web.bind.annotation.*;

import com.blog.dto.LoginDto;
import com.blog.dto.RegisterDto;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @PostMapping("/login")
    public void login(@RequestBody LoginDto req) {
        // return "login end point";
        System.out.println(req.getUsername());
        System.out.println(req.getPassword());
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterDto req) {
        // return "register end point";
        System.out.println(req.getUsername());
        System.out.println(req.getPassword());
        System.out.println(req.getEmail());
    }
}
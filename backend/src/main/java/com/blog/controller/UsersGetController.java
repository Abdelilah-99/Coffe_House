package com.blog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.blog.dto.UsersRespons;
import com.blog.service.UsersServices;

@RestController
@RequestMapping("/api/users")
public class UsersGetController {
    @Autowired
    private UsersServices userService;

    @GetMapping
    public List<UsersRespons> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/all")
    public ResponseEntity<List<UsersRespons>> getAllUsersWithResponse() {
        List<UsersRespons> users = userService.findAll();
        return ResponseEntity.ok(users);
    }
}

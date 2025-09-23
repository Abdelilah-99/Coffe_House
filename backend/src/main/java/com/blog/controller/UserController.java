package com.blog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.blog.dto.*;
import com.blog.service.UsersServices;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
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

    @PostMapping("/follow/{uuid}")
    public ResponseEntity<UserFollowRes> followUser(@PathVariable String uuid) {
        UserFollowRes result = userService.follow(uuid);
        return ResponseEntity.ok(result);
    }
}

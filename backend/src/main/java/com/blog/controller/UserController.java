package com.blog.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.blog.dto.*;
import com.blog.dto.FollowUserResponse;
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
    public ResponseEntity<?> followUser(@PathVariable String uuid) {
        return userService.follow(uuid)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/unfollow/{uuid}")
    public ResponseEntity<?> unfollowUser(@PathVariable String uuid) {
        return userService.unfollow(uuid)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/isfollowing/{uuid}")
    public ResponseEntity<?> isFollowing(@PathVariable String uuid) {
        return userService.isFollowing(uuid)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<UsersRespons>> searchForUser(@RequestParam String username) {
        List<UsersRespons> result = userService.getUser(username);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/profile/{uuid}")
    public ResponseEntity<?> profile(@PathVariable String uuid) {
        return userService.getProfile(uuid)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/me/followers")
    public ResponseEntity<List<FollowUserResponse>> getMyFollowers() {
        List<FollowUserResponse> followers = userService.getMyFollowers();
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/me/following")
    public ResponseEntity<List<FollowUserResponse>> getMyFollowing() {
        List<FollowUserResponse> following = userService.getMyFollowing();
        return ResponseEntity.ok(following);
    }
}

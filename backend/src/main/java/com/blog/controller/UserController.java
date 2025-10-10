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
    public ResponseEntity<UserFollowRes> followUser(@PathVariable String uuid) {
        UserFollowRes result = userService.follow(uuid);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/unfollow/{uuid}")
    public ResponseEntity<UserFollowRes> unfollowUser(@PathVariable String uuid) {
        UserFollowRes result = userService.unfollow(uuid);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/isfollowing/{uuid}")
    public ResponseEntity<UserFollowRes> isFollowing(@PathVariable String uuid) {
        UserFollowRes result = userService.isFollowing(uuid);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UsersRespons>> searchForUser(@RequestParam String username) {
        System.out.println("username query: " + username);
        List<UsersRespons> result = userService.getUser(username);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/profile/{uuid}")
    public ResponseEntity<UsersRespons> profile(@PathVariable String uuid) {
        UsersRespons result = userService.getProfile(uuid);
        return ResponseEntity.ok(result);
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

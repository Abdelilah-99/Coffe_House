package com.blog.controller;

import com.blog.dto.*;
import com.blog.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminPannelController {

    private AdminService adminService;

    public AdminPannelController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UsersAdmineResponse>> getUsersInfo() {
        List<UsersAdmineResponse> users = adminService.getUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/reports/posts")
    public ResponseEntity<List<ReportsAdmineResponse>> getPostsReports() {
        List<ReportsAdmineResponse> users = adminService.getPostsReports();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/reports/users")
    public ResponseEntity<List<ReportsAdmineResponse>> getUsersReports() {
        List<ReportsAdmineResponse> users = adminService.getUsersReports();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/{uuid}")
    public ResponseEntity<UsersAdmineResponse> getUser(@PathVariable String uuid) {
        UsersAdmineResponse user = adminService.getUser(uuid);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/user/delete/{uuid}")
    public ResponseEntity<UsersAdmineResponse> daleteUser(@PathVariable String uuid) {
        UsersAdmineResponse user = adminService.deleteUser(uuid);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/user/ban/{uuid}")
    public ResponseEntity<UsersAdmineResponse> banUser(@PathVariable String uuid) {
        UsersAdmineResponse user = adminService.banUser(uuid);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/post/delete/{uuid}")
    public ResponseEntity<PostRes> daletePost(@PathVariable String uuid) {
        PostRes post = adminService.deletePost(uuid);
        return ResponseEntity.ok(post);
    }

    @PostMapping("/post/hide/{uuid}")
    public ResponseEntity<PostRes> hidePost(@PathVariable String uuid) {
        PostRes post = adminService.hidePost(uuid);
        return ResponseEntity.ok(post);
    }
}

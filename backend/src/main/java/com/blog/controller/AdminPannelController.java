package com.blog.controller;

import com.blog.dto.*;
import com.blog.service.AdminService;
import com.blog.service.NotifService;
import com.blog.dto.NotificationRequest;
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
}

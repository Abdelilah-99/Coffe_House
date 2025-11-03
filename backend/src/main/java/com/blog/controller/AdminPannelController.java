package com.blog.controller;

import com.blog.dto.*;
import com.blog.service.AdminService;
import com.blog.service.AdminService.UserPage;
import com.blog.service.AdminService.PostPage;
import com.blog.service.AdminService.ReportPage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminPannelController {

    private AdminService adminService;

    public AdminPannelController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/user/{uuid}")
    public ResponseEntity<UsersAdmineResponse> getUser(@PathVariable String uuid) {
        return adminService.getUser(uuid)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/user/delete/{uuid}")
    public ResponseEntity<UsersAdmineResponse> daleteUser(@PathVariable String uuid) {
        return adminService.deleteUser(uuid)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/user/ban/{uuid}")
    public ResponseEntity<UsersAdmineResponse> banUser(@PathVariable String uuid) {
        return adminService.banUser(uuid)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping("/post/delete/{uuid}")
    public ResponseEntity<PostRes> daletePost(@PathVariable String uuid) {
        return adminService.deletePost(uuid)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/post/hide/{uuid}")
    public ResponseEntity<PostRes> hidePost(@PathVariable String uuid) {
        return adminService.hidePost(uuid)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/statistics")
    public ResponseEntity<AdminStatisticsResponse> getOverallStatistics() {
        AdminStatisticsResponse statistics = adminService.getOverallStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/users/pages")
    public ResponseEntity<UserPage> getUsersByPage(
            @RequestParam(value = "lastCreatedAt", required = false) Long lastCreatedAt,
            @RequestParam(value = "lastUuid", required = false) String lastUuid) {
        UserPage data = adminService.getUsersPaginated(lastCreatedAt, lastUuid);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/posts/pages")
    public ResponseEntity<PostPage> getPostsByPage(
            @RequestParam(value = "lastTime", required = false) Long lastTime,
            @RequestParam(value = "lastUuid", required = false) String lastUuid) {
        PostPage data = adminService.getAllPostsPaginated(lastTime, lastUuid);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/reports/posts/pages")
    public ResponseEntity<ReportPage> getPostsReportsByPage(
            @RequestParam(value = "lastTime", required = false) Long lastTime,
            @RequestParam(value = "lastUuid", required = false) String lastUuid) {
        ReportPage data = adminService.getPostsReportsPaginated(lastTime, lastUuid);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/reports/users/pages")
    public ResponseEntity<ReportPage> getUsersReportsByPage(
            @RequestParam(value = "lastTime", required = false) Long lastTime,
            @RequestParam(value = "lastUuid", required = false) String lastUuid) {
        ReportPage data = adminService.getUsersReportsPaginated(lastTime, lastUuid);
        return ResponseEntity.ok(data);
    }
}

package com.blog.controller;

import com.blog.dto.*;
import com.blog.service.AdminService;
import com.blog.service.AdminService.UserPage;
import com.blog.service.AdminService.PostPage;
import com.blog.service.AdminService.ReportPage;
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

    @GetMapping("/posts")
    public ResponseEntity<List<PostRes>> getPostsInfo() {
        List<PostRes> posts = adminService.getAllPosts();
        return ResponseEntity.ok(posts);
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

    @GetMapping("/statistics")
    public ResponseEntity<AdminStatisticsResponse> getOverallStatistics() {
        AdminStatisticsResponse statistics = adminService.getOverallStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/analytics/users/top-commenters")
    public ResponseEntity<List<TopUserResponse>> getUsersWithMostComments() {
        List<TopUserResponse> topUsers = adminService.getUsersWithMostComments();
        return ResponseEntity.ok(topUsers);
    }

    @GetMapping("/analytics/users/most-followed")
    public ResponseEntity<List<TopUserResponse>> getUsersWithMostFollowers() {
        List<TopUserResponse> topUsers = adminService.getUsersWithMostFollowers();
        return ResponseEntity.ok(topUsers);
    }

    @GetMapping("/analytics/users/most-reported")
    public ResponseEntity<List<TopUserResponse>> getMostReportedUsers() {
        List<TopUserResponse> topUsers = adminService.getMostReportedUsers();
        return ResponseEntity.ok(topUsers);
    }

    @GetMapping("/analytics/posts/most-commented")
    public ResponseEntity<List<TopPostResponse>> getPostsWithMostComments() {
        List<TopPostResponse> topPosts = adminService.getPostsWithMostComments();
        return ResponseEntity.ok(topPosts);
    }

    @GetMapping("/analytics/posts/most-liked")
    public ResponseEntity<List<TopPostResponse>> getPostsWithMostLikes() {
        List<TopPostResponse> topPosts = adminService.getPostsWithMostLikes();
        return ResponseEntity.ok(topPosts);
    }

    @GetMapping("/analytics/posts/most-reported")
    public ResponseEntity<List<TopPostResponse>> getMostReportedPosts() {
        List<TopPostResponse> topPosts = adminService.getMostReportedPosts();
        return ResponseEntity.ok(topPosts);
    }

    @GetMapping("/users/pages")
    public ResponseEntity<UserPage> getUsersByPage(
            @RequestParam(value = "lastUuid", required = false) String lastUuid) {
        UserPage data = adminService.getUsersPaginated(lastUuid);
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

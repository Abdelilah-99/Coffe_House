package com.blog.dto;

public class AdminStatisticsResponse {
    private long totalUsers;
    private long totalPosts;
    private long totalReports;
    private String message;

    public AdminStatisticsResponse() {
    }

    public AdminStatisticsResponse(long totalUsers, long totalPosts, long totalReports, String message) {
        this.totalUsers = totalUsers;
        this.totalPosts = totalPosts;
        this.totalReports = totalReports;
        this.message = message;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalPosts() {
        return totalPosts;
    }

    public void setTotalPosts(long totalPosts) {
        this.totalPosts = totalPosts;
    }

    public long getTotalReports() {
        return totalReports;
    }

    public void setTotalReports(long totalReports) {
        this.totalReports = totalReports;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

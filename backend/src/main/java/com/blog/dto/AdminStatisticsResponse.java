package com.blog.dto;

public class AdminStatisticsResponse {
    private long totalUsers;
    private long totalPosts;
    private long totalComments;
    private long totalReports;
    private long totalFollows;
    private String message;

    public AdminStatisticsResponse() {
    }

    public AdminStatisticsResponse(long totalUsers, long totalPosts, long totalComments,
            long totalReports, long totalFollows, String message) {
        this.totalUsers = totalUsers;
        this.totalPosts = totalPosts;
        this.totalComments = totalComments;
        this.totalReports = totalReports;
        this.totalFollows = totalFollows;
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

    public long getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(long totalComments) {
        this.totalComments = totalComments;
    }

    public long getTotalReports() {
        return totalReports;
    }

    public void setTotalReports(long totalReports) {
        this.totalReports = totalReports;
    }

    public long getTotalFollows() {
        return totalFollows;
    }

    public void setTotalFollows(long totalFollows) {
        this.totalFollows = totalFollows;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

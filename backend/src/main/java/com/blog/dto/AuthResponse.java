package com.blog.dto;

public class AuthResponse {
    private String message;
    private String userRole;
    private String userName;
    private String token;

    public AuthResponse(String message, String userRole, String userName, String token) {
        this.message = message;
        this.userRole = userRole;
        this.userName = userName;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserRole() {
        return userRole;
    }
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}

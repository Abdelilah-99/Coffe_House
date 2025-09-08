package com.blog.dto;

public class AuthResponse {
    private String message;
    private String userRole;
    private String userName;

    public AuthResponse(String message, String userRole, String userName) {
        this.message = message;
        this.userRole = userRole;
        this.userName = userName;
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
}
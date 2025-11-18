package com.blog.dto;

public class UpdateProfileResponse {
    private String message;
    private UsersRespons user;

    public UpdateProfileResponse() {
    }

    public UpdateProfileResponse(String message, UsersRespons user) {
        this.message = message;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UsersRespons getUser() {
        return user;
    }

    public void setUser(UsersRespons user) {
        this.user = user;
    }
}


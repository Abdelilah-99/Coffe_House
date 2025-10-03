package com.blog.dto;

import java.util.List;

import com.blog.entity.Post;

public class UsersAdmineResponse {
    private String uuid;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String message;
    private List<Post> posts;

    public UsersAdmineResponse() {
    }

    public UsersAdmineResponse(
            String uuid,
            String firstName,
            String lastName,
            String username,
            String email,
            String message,
            List<Post> posts) {
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.message = message;
        this.posts = posts;
    }

    public List<Post> getPost() {
        return posts;
    }

    public void setPost(List<Post> posts) {
        this.posts = posts;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

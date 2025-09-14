package com.blog.dto;

import com.blog.entity.User;

public class CreatePostRes {
    private User user;
    private String content;
    private String title;
    private String message;
    private String timestamp;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public CreatePostRes() {
    }

    public CreatePostRes(User user, String content, String title, String message, String timestamp) {
        this.user = user;
        this.content = content;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUserId(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

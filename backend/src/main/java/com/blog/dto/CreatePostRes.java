package com.blog.dto;

import com.blog.entity.User;

public class CreatePostRes {
    private String userName;
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

    public CreatePostRes(String userName, String content, String title, String message, String timestamp) {
        this.userName = userName;
        this.content = content;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

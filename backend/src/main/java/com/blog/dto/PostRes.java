package com.blog.dto;

import java.util.List;

public class PostRes {
    private long id;
    private long userId;
    private String userName;
    private String content;
    private String title;
    private String message;
    private String timestamp;
    private List<String> mediaPaths;

    public PostRes() {
    }

    public PostRes(
            long id,
            long userId,
            String userName,
            String content,
            String title,
            String message,
            String timestamp,
            List<String> mediaPaths) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.mediaPaths = mediaPaths;
    }

    public List<String> getMediaPaths() {
        return mediaPaths;
    }

    public void setMediaPaths(List<String> mediaPaths) {
        this.mediaPaths = mediaPaths;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return this.userId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
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

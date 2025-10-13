package com.blog.dto;

import java.util.List;

public class PostRes {
    private String postUuid;
    private String userUuid;
    private String userName;
    private String content;
    private String title;
    private String message;
    private long timestamp;
    private List<String> mediaPaths;
    private long commentCount;
    private long likeCount;
    private String profileImagePath;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PostRes() {
    }

    public PostRes(
            String postUuid,
            String userUuid,
            String userName,
            String content,
            String title,
            String message,
            long timestamp,
            List<String> mediaPaths,
            long commentCount,
            long likeCount,
            String profileImagePath,
            String status) {
        this.postUuid = postUuid;
        this.userUuid = userUuid;
        this.userName = userName;
        this.content = content;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.mediaPaths = mediaPaths;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.profileImagePath = profileImagePath;
        this.status = status;
    }

    public long getCommentCount() {
        return this.commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    public long getLikeCount() {
        return this.likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public List<String> getMediaPaths() {
        return mediaPaths;
    }

    public void setMediaPaths(List<String> mediaPaths) {
        this.mediaPaths = mediaPaths;
    }

    public void setPostUuid(String postUuid) {
        this.postUuid = postUuid;
    }

    public String getPostUuid() {
        return this.postUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getUserUuid() {
        return this.userUuid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
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

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }
}

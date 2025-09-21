package com.blog.dto;

public class CommentPostRes {
    private String postUuid;
    private String userUuid;
    private String comment;
    private String message;

    public CommentPostRes() {
    }

    public CommentPostRes(String postUuid, String userUuid, String comment, String message) {
        this.postUuid = postUuid;
        this.userUuid = userUuid;
        this.comment = comment;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPostUuid() {
        return this.postUuid;
    }

    public void setPostUuid(String postUuid) {
        this.postUuid = postUuid;
    }

    public String getUserUuid() {
        return this.userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return this.comment;
    }
}

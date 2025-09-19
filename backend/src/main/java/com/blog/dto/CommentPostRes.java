package com.blog.dto;

public class CommentPostRes {
    private long postId;
    private long userId;
    private String comment;
    private String message;

    public CommentPostRes() {
    }

    public CommentPostRes(long postId, long userId, String comment, String message) {
        this.postId = postId;
        this.userId = userId;
        this.comment = comment;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getPostId() {
        return this.postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public long getUserId() {
        return this.userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return this.comment;
    }
}

package com.blog.dto;

public class CommentPostReq {
    private String comment;

    public CommentPostReq() {}

    public CommentPostReq(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

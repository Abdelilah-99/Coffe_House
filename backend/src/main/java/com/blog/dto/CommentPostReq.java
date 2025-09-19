package com.blog.dto;

public class CommentPostReq {
    private long postId;
    private String comment;

    public CommentPostReq() {}

    public CommentPostReq(String comment) {
        this.comment = comment;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

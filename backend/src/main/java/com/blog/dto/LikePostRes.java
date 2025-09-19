package com.blog.dto;

public class LikePostRes {
    private long userId, postId, likeCount;

    public LikePostRes() {
    }

    public LikePostRes(long userId, long postId, long likeCount) {
        this.userId = userId;
        this.postId = postId;
        this.likeCount = likeCount;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }
}

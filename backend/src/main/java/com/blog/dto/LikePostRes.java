package com.blog.dto;

public class LikePostRes {
    private String userUuid, postUuid;
    private long likeCount;

    public LikePostRes() {
    }

    public LikePostRes(String userUuid, String postUuid, long likeCount) {
        this.userUuid = userUuid;
        this.postUuid = postUuid;
        this.likeCount = likeCount;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getPostUuid() {
        return postUuid;
    }

    public void setPostUuid(String postUuid) {
        this.postUuid = postUuid;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }
}

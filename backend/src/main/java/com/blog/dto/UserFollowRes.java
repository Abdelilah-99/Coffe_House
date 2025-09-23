package com.blog.dto;

public class UserFollowRes {
    private String followerUuid;
    private String followingUuid;
    private String message;

    public UserFollowRes() {}

    public UserFollowRes(String followerUuid, String followingUuid, String message) {
        this.followerUuid = followerUuid;
        this.followingUuid = followingUuid;
        this.message = message;
    }

    public String getFollowerUuid() {
        return followerUuid;
    }

    public void setFollowerUuid(String followerUuid) {
        this.followerUuid = followerUuid;
    }

    public String getFollowingUuid() {
        return followingUuid;
    }

    public void setFollowingUuid(String followingUuid) {
        this.followingUuid = followingUuid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package com.blog.dto;

public class UserFollowRes {
    private long follower;
    private long following;
    private String followerUuid;
    private String followingUuid;
    private String message;

    public UserFollowRes() {
    }

    public UserFollowRes(long follower, long following, String message, String followerUuid, String followingUuid) {
        this.follower = follower;
        this.following = following;
        this.followerUuid = followerUuid;
        this.followingUuid = followingUuid;
        this.message = message;
    }

    public long getFollower() {
        return follower;
    }

    public void setFollower(long follower) {
        this.follower = follower;
    }

    public long getFollowing() {
        return following;
    }

    public void setFollowing(long following) {
        this.following = following;
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

package com.blog.entity;

import jakarta.persistence.*;

@Entity
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;
    @ManyToOne
    @JoinColumn(name = "following_id", nullable = false)
    private User following;

    public Follow() {}

    public Follow(User follower, User following) {
        this.follower = follower;
        this.following = following;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getFollower() {
        return follower;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }

    public User getFollowing() {
        return following;
    }

    public void setFollowing(User following) {
        this.following = following;
    }
}

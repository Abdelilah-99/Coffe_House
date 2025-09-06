package com.blog.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follwer;
    @ManyToOne
    @JoinColumn(name = "following_id", nullable = false)
    private User following;
}

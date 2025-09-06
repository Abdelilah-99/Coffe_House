package com.blog.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String reason;
    private String time;
    private String typeReport;
    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private User reporterId;
    @ManyToOne
    @JoinColumn(name = "reported_user_id", nullable = true)
    private User reportedUserId;
    @ManyToOne
    @JoinColumn(name = "reported_post_id", nullable = true)
    private Post reportedPostrId;
}

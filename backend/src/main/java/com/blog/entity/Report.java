package com.blog.entity;

import java.util.UUID;

import jakarta.persistence.*;

@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reason;
    private Long createdAt;
    // private String typeReport;
    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private User reporterId;
    @ManyToOne
    @JoinColumn(name = "reported_user_id", nullable = true)
    private User user;
    @ManyToOne
    @JoinColumn(name = "reported_post_id", nullable = true)
    private Post post;
    @Column(nullable = false, unique = true, updatable = false)
    private String uuid = UUID.randomUUID().toString();

    public Report() {
    }

    public Report(String reason, long createdAt, User reporterId, String uuid) {
        this.reason = reason;
        this.createdAt = createdAt;
        // this.typeReport = typeReport;
        this.reporterId = reporterId;
        this.uuid = uuid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    // public String getTypeReport() {
    //     return typeReport;
    // }

    // public void setTypeReport(String typeReport) {
    //     this.typeReport = typeReport;
    // }

    public User getReporterId() {
        return reporterId;
    }

    public void setReporterId(User reporterId) {
        this.reporterId = reporterId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}

package com.blog.entity;

import java.util.UUID;

import jakarta.persistence.*;

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
    private Post reportedPostId;
    @Column(nullable = false, unique = true, updatable = false)
    private String uuid = UUID.randomUUID().toString();

    public Report() {
    }

    public Report(String reason, String time, String typeReport, User reporterId, String uuid) {
        this.reason = reason;
        this.time = time;
        this.typeReport = typeReport;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTypeReport() {
        return typeReport;
    }

    public void setTypeReport(String typeReport) {
        this.typeReport = typeReport;
    }

    public User getReporterId() {
        return reporterId;
    }

    public void setReporterId(User reporterId) {
        this.reporterId = reporterId;
    }

    public User getReportedUserId() {
        return reportedUserId;
    }

    public void setReportedUserId(User reportedUserId) {
        this.reportedUserId = reportedUserId;
    }

    public Post getReportedPostId() {
        return reportedPostId;
    }

    public void setReportedPostId(Post reportedPostId) {
        this.reportedPostId = reportedPostId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}

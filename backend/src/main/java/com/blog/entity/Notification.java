package com.blog.entity;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String userUuid;
    private String postOrProfileUuid;
    private String notification;
    @Column(name = "created_at")
    private long createdAt = System.currentTimeMillis();
    @Column(name = "is_read")
    private boolean isRead = false;
    private String uuid = UUID.randomUUID().toString();

    public Notification() {
    }

    public Notification(String userUuid, String notification, long createdAt, String postOrProfileUuid) {
        this.userUuid = userUuid;
        this.notification = notification;
        this.createdAt = createdAt;
        this.uuid = UUID.randomUUID().toString();
        this.postOrProfileUuid = postOrProfileUuid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPostOrProfileUuid() {
        return postOrProfileUuid;
    }

    public void setPostOrProfileUuid(String postOrProfileUuid) {
        this.postOrProfileUuid = postOrProfileUuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }
}

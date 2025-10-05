package com.blog.entity;

import java.util.UUID;
import jakarta.persistence.*;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String notificatedUser;
    private String postOrProfileUuid;
    private String notification;
    @Column(name = "created_at")
    private long createdAt = System.currentTimeMillis();
    @Column(name = "is_read")
    private boolean isRead = false;
    private String uuid = UUID.randomUUID().toString();
    private String notificationOwner;

    public Notification() {
    }

    public Notification(String notificatedUser,
            String notification,
            long createdAt,
            String postOrProfileUuid,
            String notificatioOwner) {
        this.notificatedUser = notificatedUser;
        this.notification = notification;
        this.createdAt = createdAt;
        this.notificationOwner = notificatioOwner;
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

    public String getNotificatedUser() {
        return notificatedUser;
    }

    public void setNotificatedUser(String notificatedUser) {
        this.notificatedUser = notificatedUser;
    }

    public String getNotificationOwner() {
        return notificationOwner;
    }

    public void setNotificationOwner(String notificationOwner) {
        this.notificationOwner = notificationOwner;
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

    public boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }
}

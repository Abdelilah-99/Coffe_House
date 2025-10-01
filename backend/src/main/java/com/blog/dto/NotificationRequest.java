package com.blog.dto;

public class NotificationRequest {
    private String uuid;

    public NotificationRequest() {
    }

    public NotificationRequest(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}

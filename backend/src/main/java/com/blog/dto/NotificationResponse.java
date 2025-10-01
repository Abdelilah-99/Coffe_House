package com.blog.dto;

public class NotificationResponse {
    private String uuid;
    private String content;
    private long time;

    public NotificationResponse() {
    }

    public NotificationResponse(String uuid, String content, long time) {
        this.uuid = uuid;
        this.content = content;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

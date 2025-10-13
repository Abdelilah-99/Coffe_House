package com.blog.dto;

public class NotificationResponse {
    private String uuid;
    private String content;
    private String message;
    private long time;
    private String postOrProfileUuid;
    private boolean isRead;

    public NotificationResponse() {
    }

    public NotificationResponse(
            String uuid,
            String postOrProfileUuid,
            String content,
            String message,
            long time,
            boolean isRead) {
        this.uuid = uuid;
        this.content = content;
        this.message = message;
        this.time = time;
        this.postOrProfileUuid = postOrProfileUuid;
        this.isRead = isRead;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getIsRead() {
        return this.isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

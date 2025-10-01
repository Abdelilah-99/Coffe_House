package com.blog.dto;

public class NotificationResponse {
    private String uuid;
    private String postOrProfileUuid;
    private String content;
    private long time;

    public NotificationResponse() {
    }

    public NotificationResponse(String uuid, String postOrProfileUuid, String content, long time) {
        this.uuid = uuid;
        this.content = content;
        this.time = time;
        this.postOrProfileUuid = postOrProfileUuid;
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

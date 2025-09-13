package com.blog.dto;

public class CreatePostReq {
    private String content;
    private String media;
    private String title;
    private long userId;

    public CreatePostReq() {}

    public CreatePostReq(String content, String media, String title, long userId) {
        this.content = content;
        this.media = media;
        this.title = title;
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}

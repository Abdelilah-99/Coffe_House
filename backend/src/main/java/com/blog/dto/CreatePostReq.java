package com.blog.dto;

public class CreatePostReq {
    private String content;
    private String media;
    private String title;

    public CreatePostReq() {
    }

    public CreatePostReq(String content, String media, String title) {
        this.content = content;
        this.media = media;
        this.title = title;
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

}

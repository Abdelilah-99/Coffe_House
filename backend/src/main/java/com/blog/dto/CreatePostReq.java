package com.blog.dto;

import org.springframework.web.multipart.MultipartFile;

public class CreatePostReq {
    private String content;
    private String title;
    private MultipartFile[] mediaFiles;

    public CreatePostReq() {
    }

    public CreatePostReq(String content, String title, MultipartFile[] mediaFiles) {
        this.content = content;
        this.title = title;
        this.mediaFiles = mediaFiles;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MultipartFile[] getMediaFiles() {
        return mediaFiles;
    }

    public void setMediaFiles(MultipartFile[] mediaFiles) {
        this.mediaFiles = mediaFiles;
    }

}

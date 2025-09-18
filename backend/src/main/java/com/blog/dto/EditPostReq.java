package com.blog.dto;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public class EditPostReq {
    private String content;
    private String title;
    private MultipartFile[] mediaFiles;
    private List<String> pathFiles;

    public EditPostReq() {
    }

    public EditPostReq(String content, String title, MultipartFile[] mediaFiles, List<String> pathFiles) {
        this.content = content;
        this.title = title;
        this.mediaFiles = mediaFiles;
        this.pathFiles = pathFiles;
    }

    public String getContent() {
        return content;
    }

    public void setPathFiles(List<String> pathFiles) {
        this.pathFiles = pathFiles;
    }

    public List<String> getPathFiles() {
        return this.pathFiles;
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

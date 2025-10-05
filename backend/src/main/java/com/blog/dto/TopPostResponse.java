package com.blog.dto;

public class TopPostResponse {
    private String uuid;
    private String title;
    private String content;
    private String authorUsername;
    private String authorUuid;
    private long commentCount;
    private long likeCount;
    private long reportCount;
    private String message;

    public TopPostResponse() {
    }

    public TopPostResponse(String uuid, String title, String content, String authorUsername,
            String authorUuid, long commentCount, long likeCount,
            long reportCount, String message) {
        this.uuid = uuid;
        this.title = title;
        this.content = content;
        this.authorUsername = authorUsername;
        this.authorUuid = authorUuid;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.reportCount = reportCount;
        this.message = message;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getAuthorUuid() {
        return authorUuid;
    }

    public void setAuthorUuid(String authorUuid) {
        this.authorUuid = authorUuid;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public long getReportCount() {
        return reportCount;
    }

    public void setReportCount(long reportCount) {
        this.reportCount = reportCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

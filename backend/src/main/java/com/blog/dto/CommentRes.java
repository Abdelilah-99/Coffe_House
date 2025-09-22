package com.blog.dto;

import com.blog.entity.*;
import java.util.*;

public class CommentRes {
    private String postUuid;
    private String userUuid;
    private String timesTamp;
    private List<Comment> comments;

    public CommentRes() {
    }

    public CommentRes(
            String postUuid,
            String userUuid,
            String timesTamp,
            List<Comment> comments) {
        this.postUuid = postUuid;
        this.userUuid = userUuid;
        this.timesTamp = timesTamp;
        this.comments = comments;
    }

    public void setTimesTamp(String timesTamp) {
        this.timesTamp = timesTamp;
    }

    public String getTimesTamp() {
        return this.timesTamp;
    }

    public void setPostUuid(String postUuid) {
        this.postUuid = postUuid;
    }

    public String getPostUuid() {
        return this.postUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getUserUuid() {
        return this.userUuid;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Comment> getComments() {
        return this.comments;
    }
}

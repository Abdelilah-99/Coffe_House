package com.blog.dto;

import com.blog.entity.*;
import java.util.*;

public class CommentRes {
    private String userName;
    private String postUuid;
    private String userUuid;
    private List<Comment> comments;

    public CommentRes() {
    }

    public CommentRes(
            String userName,
            String postUuid,
            String userUuid,
            List<Comment> comments) {
        this.userName = userName;
        this.postUuid = postUuid;
        this.userUuid = userUuid;
        this.comments = comments;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName(String userName) {
        return this.userName;
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

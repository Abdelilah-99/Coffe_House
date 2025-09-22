package com.blog.entity;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private long id;
    @Column(columnDefinition = "TEXT")
    private String comment;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "post_id")
    private Post post;
    @Column(nullable = false, unique = true, updatable = false)
    private String uuid = UUID.randomUUID().toString();
    private String timestamp;

    public Comment() {
    }

    public Comment(String comment, User user, Post post, String uuid, String timesTamp) {
        this.comment = comment;
        this.user = user;
        this.post = post;
        this.uuid = uuid;
        this.timestamp = timesTamp;
    }

    public String getTimesTamp() {
        return timestamp;
    }

    public void setTimesTamp(String timesTamp) {
        this.timesTamp = timesTamp;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}

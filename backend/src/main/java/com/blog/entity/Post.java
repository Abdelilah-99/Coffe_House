package com.blog.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    @Column(columnDefinition = "TEXT")
    private String mediaPaths;
    private String timestamp;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Like> likes;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(nullable = false, unique = true, updatable = false)
    private String uuid = UUID.randomUUID().toString();

    public Post() {
    }

    public Post(String title, String content, String mediaPaths, User user, String timestamp, String uuid) {
        this.title = title;
        this.content = content;
        this.mediaPaths = mediaPaths;
        this.user = user;
        this.timestamp = timestamp;
        this.uuid = uuid;
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

    public void setId(Long id) {
        this.id = id;
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

    public List<String> getMediaPaths() {
        if (this.mediaPaths == null || this.mediaPaths.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(mediaPaths.split(","));
    }

    public void setMediaPaths(List<String> mediaPaths) {
        if (mediaPaths.isEmpty()) {
            this.mediaPaths = null;
            return;
        }
        this.mediaPaths = String.join(",", mediaPaths);
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}

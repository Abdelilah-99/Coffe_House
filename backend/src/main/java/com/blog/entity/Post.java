package com.blog.entity;

import java.util.List;
import jakarta.persistence.*;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private String media;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Like> likes;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

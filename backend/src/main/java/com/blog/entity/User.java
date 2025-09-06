package com.blog.entity;

import java.util.*;
import jakarta.persistence.*;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String role;
    private String password;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Follow> following;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Follow> followers;
}

package com.blog.repository;

import com.blog.entity.Post;
import com.blog.entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
        List<Post> findByUser(User user);
}

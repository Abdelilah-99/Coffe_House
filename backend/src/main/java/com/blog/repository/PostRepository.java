package com.blog.repository;

import com.blog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByEmail(String email);

    Optional<Post> findByUserName(String userName);
}

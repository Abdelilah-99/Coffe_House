package com.blog.repository;

import com.blog.entity.Comment;
import com.blog.entity.User;
import com.blog.entity.Post;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    long countByPostId(long id);

    long countByPost_uuid(String uuid);

    Optional<Comment> findByUuid(String uuid);
    
    long countByUser(User user);
    
    long countByPost(Post post);
}

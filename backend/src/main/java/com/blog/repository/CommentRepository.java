package com.blog.repository;

import com.blog.entity.Comment;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    long countByPostId(long id);

    long countByPost_uuid(String uuid);

    Optional<Comment> findByUuid(String uuid);
}

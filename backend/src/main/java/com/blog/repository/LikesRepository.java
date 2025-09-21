package com.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.blog.entity.*;

@Repository
public interface LikesRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    void deleteByUserIdAndPostId(Long userId, Long postId);

    void deleteByUser_uuidAndPost_uuid(String userUuid, String postUuid);

    boolean existsByUser_uuidAndPost_uuid(String userUuid, String postUuid);

    long countByPostId(Long postId);

    long countByPost_uuid(String uuid);
}

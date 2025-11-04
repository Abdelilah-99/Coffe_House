package com.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.blog.entity.*;

@Repository
public interface LikesRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    @Modifying
    @Query(value = "delete from likes where user_id = :userId and post_id = :postId", nativeQuery = true)
    void deleteByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);

    @Modifying
    @Query(value = "insert into likes (user_id, post_id) values (:userId, :postId)", nativeQuery = true)
    void insertLike(@Param("userId") long userId, @Param("postId") long postId);

    void deleteByUser_uuidAndPost_uuid(String userUuid, String postUuid);

    boolean existsByUser_uuidAndPost_uuid(String userUuid, String postUuid);

    long countByPostId(Long postId);

    long countByPost_uuid(String uuid);
}

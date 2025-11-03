package com.blog.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.blog.entity.*;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    @Transactional
    @Modifying
    @Query(value = "delete from follow where follower_id = :followerId and following_id = :followingId", nativeQuery = true)
    void deleteByFollowerIdAndFollowingId(@Param("followerId") Long followerId,
            @Param("followingId") Long followingId);

    boolean existsByFollowerIdAndFollowingId(long follwer, long following);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO follow (follower_id, following_id) VALUES (:followerId, :followingId)", nativeQuery = true)
    void insertFollow(@Param("followerId") Long followerId,
            @Param("followingId") Long followingId);

    long countByFollowerId(long id);

    long countByFollowingId(long id);

    @Query("SELECT f.follower FROM Follow f WHERE f.following.id = :userId")
    List<User> findFollowersByUserId(@Param("userId") Long userId);

    @Query("SELECT f.following FROM Follow f WHERE f.follower.id = :userId")
    List<User> findFollowingByUserId(@Param("userId") Long userId);
}

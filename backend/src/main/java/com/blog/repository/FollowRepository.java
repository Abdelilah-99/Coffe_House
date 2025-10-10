package com.blog.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.blog.entity.*;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    @Transactional
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);

    boolean existsByFollowerIdAndFollowingId(long follwer, long following);

    long countByFollowerId(long id);
    long countByFollowingId(long id);

    @Query("SELECT f.follower FROM Follow f WHERE f.following.id = :userId")
    List<User> findFollowersByUserId(@Param("userId") Long userId);

    @Query("SELECT f.following FROM Follow f WHERE f.follower.id = :userId")
    List<User> findFollowingByUserId(@Param("userId") Long userId);
}

package com.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
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
}

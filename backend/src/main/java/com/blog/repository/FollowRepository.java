package com.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.blog.entity.*;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);
}

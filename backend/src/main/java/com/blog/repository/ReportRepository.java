package com.blog.repository;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.blog.entity.Report;
import com.blog.entity.User;
import com.blog.entity.Post;
import org.springframework.data.domain.Pageable;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByPostIsNotNull();

    List<Report> findByUserIsNotNull();
    
    long countByUser(User user);

    long countByPost(Post post);

    @Query("""
            SELECT r FROM Report r
            WHERE r.post IS NOT NULL
            AND (r.createdAt < :lastTime
            OR (r.createdAt = :lastTime AND r.id < :lastId))
            ORDER BY r.createdAt DESC, r.id DESC
        """)
    List<Report> findByPostIsNotNullPaginated(
        @Param("lastTime") Long lastTime,
        @Param("lastId") Long lastId,
        Pageable pageable);

    @Query("""
            SELECT r FROM Report r
            WHERE r.user IS NOT NULL
            AND (r.createdAt < :lastTime
            OR (r.createdAt = :lastTime AND r.id < :lastId))
            ORDER BY r.createdAt DESC, r.id DESC
        """)
    List<Report> findByUserIsNotNullPaginated(
        @Param("lastTime") Long lastTime,
        @Param("lastId") Long lastId,
        Pageable pageable);
}

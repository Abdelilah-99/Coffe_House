package com.blog.repository;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.blog.entity.Report;
import com.blog.entity.User;
import com.blog.entity.Post;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByReportedPostIdIsNotNull();

    List<Report> findByReportedUserIdIsNotNull();
    
    long countByReportedUserId(User user);
    
    long countByReportedPostId(Post post);
}

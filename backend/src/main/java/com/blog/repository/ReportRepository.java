package com.blog.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.blog.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

}

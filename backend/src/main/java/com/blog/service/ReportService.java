package com.blog.service;

import com.blog.dto.RegisterResponse;
import com.blog.dto.UsersRespons;
import com.blog.entity.Report;
import com.blog.entity.User;
import com.blog.entity.Post;
import com.blog.repository.PostRepository;
import com.blog.repository.ReportRepository;
import com.blog.repository.UserRepository;
import com.blog.service.UsersServices;
import org.springframework.stereotype.Service;
import com.blog.dto.ReportResponse;
import com.blog.exceptions.UserNotFoundException;
import com.blog.exceptions.UserNotLoginException;
import com.blog.dto.ReportRequest;
import com.blog.exceptions.PostNotFoundException;
import com.blog.exceptions.ReportException;

@Service
public class ReportService {
    UsersServices usersServices;
    UserRepository userRepository;
    ReportRepository reportRepository;
    PostRepository postRepository;

    public ReportService(
            UsersServices usersServices,
            UserRepository userRepository,
            ReportRepository reportRepository,
            PostRepository postRepository) {
        this.usersServices = usersServices;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.postRepository = postRepository;
    }

    public ReportResponse reportProfile(String uuid, ReportRequest reason) {
        UsersRespons usersRespons;
        try {
            usersRespons = usersServices.getCurrentUser();
        } catch (Exception e) {
            throw new UserNotFoundException("user not found for reporting");
        }
        User user = userRepository.findByUuid(uuid).orElseThrow(() -> {
            throw new UserNotFoundException("user not found for reporting");
        });
        User crrUser = userRepository.findByUserName(usersRespons.getUsername()).orElseThrow(() -> {
            throw new UserNotLoginException("user not logged in or not rregistred");
        });
        if (reason.getReason() == null || reason.getReason().trim().isEmpty()) {
            throw new ReportException("reason must no be empty or null");
        }
        Report newReport = new Report();
        newReport.setReason(reason.getReason());
        newReport.setReportedUserId(user);
        newReport.setReporterId(crrUser);
        newReport.setTime(System.currentTimeMillis());
        newReport.setTypeReport("Profile");
        reportRepository.save(newReport);
        return new ReportResponse("the report has created successfully");
    }

    public ReportResponse reportPost(String uuid, ReportRequest reason) {
        UsersRespons usersRespons;
        try {
            usersRespons = usersServices.getCurrentUser();
        } catch (Exception e) {
            throw new UserNotFoundException("user not found for reporting");
        }
        Post post = postRepository.findByUuid(uuid).orElseThrow(() -> {
            throw new PostNotFoundException("post not found for reporting");
        });
        User crrUser = userRepository.findByUserName(usersRespons.getUsername()).orElseThrow(() -> {
            throw new UserNotLoginException("user not logged in or not registred");
        });
        if (reason.getReason() == null || reason.getReason().trim().isEmpty()) {
            throw new ReportException("reason must not be empty or null");
        }
        Report newReport = new Report();
        newReport.setReason(reason.getReason());
        newReport.setReportedPostId(post);
        newReport.setReporterId(crrUser);
        newReport.setTime(System.currentTimeMillis());
        newReport.setTypeReport("Post");
        reportRepository.save(newReport);
        return new ReportResponse("the report has created successfully");
    }
}

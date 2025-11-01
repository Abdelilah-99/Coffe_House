package com.blog.service;

import com.blog.dto.UsersRespons;
import com.blog.entity.Report;
import com.blog.entity.User;
import com.blog.entity.Post;
import com.blog.repository.PostRepository;
import com.blog.repository.ReportRepository;
import com.blog.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.blog.dto.ReportResponse;
import com.blog.exceptions.UserNotFoundException;
import com.blog.exceptions.UserNotLoginException;
import com.blog.dto.ReportRequest;
import com.blog.exceptions.PostNotFoundException;
import com.blog.exceptions.ReportException;

@Service
public class ReportService {
    private final UsersServices usersServices;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final PostRepository postRepository;

    ReportService(
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

        if (usersRespons.getRole().equals("ROLE_ADMIN")) {
            throw new SecurityException("Admin can directly manage profils by banning or deleting");
        }

        User user = userRepository.findByUuid(uuid).orElseThrow(() -> {
            throw new UserNotFoundException("User not available");
        });

        if (user.getStatus() != null && user.getStatus().equalsIgnoreCase("BAN")) {
            throw new ReportException("This user is already banned");
        }

        User crrUser = userRepository.findByUserName(usersRespons.getUsername()).orElseThrow(() -> {
            throw new UserNotLoginException("user not logged in or not rregistred");
        });
        if (reason.getReason() == null || reason.getReason().trim().isEmpty()) {
            throw new ReportException("reason must no be empty or null");
        }
        if (user.getId() == crrUser.getId()) {
            throw new ReportException("you cant report yourself");
        }
        Report newReport = new Report();
        newReport.setReason(reason.getReason());
        newReport.setUser(user);
        newReport.setReporterId(crrUser);
        newReport.setCreatedAt(System.currentTimeMillis());
        // newReport.setTypeReport("Profile");
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

        System.out.println("usersRespons.getRole(): " + usersRespons.getRole() );
        if (usersRespons.getRole().equals("ROLE_ADMIN")) {
            throw new SecurityException("Admin can directly manage posts by hiding or deleting");
        }

        Post post = postRepository.findByUuid(uuid).orElseThrow(() -> {
            throw new PostNotFoundException("post not found for reporting");
        });

        User postOwner = post.getUser();
        if (postOwner.getStatus() != null && postOwner.getStatus().equalsIgnoreCase("BAN")) {
            throw new ReportException("This user is already banned");
        }

        User crrUser = userRepository.findByUserName(usersRespons.getUsername()).orElseThrow(() -> {
            throw new UserNotLoginException("user not logged in or not registred");
        });
        if (reason.getReason() == null || reason.getReason().trim().isEmpty()) {
            throw new ReportException("reason must not be empty or null");
        }
        if (post.getUser().getId() == crrUser.getId()) {
            throw new ReportException("you can't report youre own post");
        }
        Report newReport = new Report();
        newReport.setReason(reason.getReason());
        newReport.setPost(post);
        newReport.setReporterId(crrUser);
        newReport.setCreatedAt(System.currentTimeMillis());
        // newReport.setTypeReport("Post");
        reportRepository.save(newReport);
        return new ReportResponse("the report has created successfully");
    }
}

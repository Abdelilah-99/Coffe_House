package com.blog.service;

import java.util.Optional;
import com.blog.dto.UsersRespons;
import com.blog.entity.Report;
import com.blog.entity.User;
import com.blog.entity.Post;
import com.blog.repository.PostRepository;
import com.blog.repository.ReportRepository;
import com.blog.repository.UserRepository;

import org.springframework.stereotype.Service;
import com.blog.dto.ReportResponse;
import com.blog.exceptions.UserNotLoginException;
import com.blog.dto.ReportRequest;
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

    public Optional<ReportResponse> reportProfile(String uuid, ReportRequest reason) {
        UsersRespons usersRespons;
        try {
            usersRespons = usersServices.getCurrentUser();
        } catch (Exception e) {
            throw new UserNotLoginException("user not found for reporting");
        }
        Optional<User> userOpt = userRepository.findByUuid(uuid);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        User user = userOpt.get();
        if (reason.getReason().length() > 200) {
            throw new ReportException("reason must be under 200 char");
        }

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
        reportRepository.save(newReport);
        return Optional.of(new ReportResponse("the report has created successfully"));
    }

    public Optional<ReportResponse> reportPost(String uuid, ReportRequest reason) {
        UsersRespons usersRespons;
        try {
            usersRespons = usersServices.getCurrentUser();
        } catch (Exception e) {
            throw new UserNotLoginException("user not found for reporting");
        }
        Optional<Post> postOpt = postRepository.findByUuid(uuid);
        if (postOpt.isEmpty()) {
            return Optional.empty();
        }
        Post post = postOpt.get();
        if (reason.getReason().length() > 200) {
            throw new ReportException("reason must be under 200 char");
        }
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
        reportRepository.save(newReport);
        return Optional.of(new ReportResponse("the report has created successfully"));
    }
}

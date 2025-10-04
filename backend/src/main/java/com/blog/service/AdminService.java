package com.blog.service;

import com.blog.entity.User;
import com.blog.dto.PostRes;
import com.blog.dto.ReportsAdmineResponse;
import com.blog.entity.Post;
import com.blog.entity.Report;
import com.blog.dto.UsersAdmineResponse;
import com.blog.dto.UsersRespons;
import com.blog.exceptions.PostNotFoundException;
import com.blog.exceptions.UserNotFoundException;
import com.blog.exceptions.UserNotLoginException;
import com.blog.exceptions.BanException;
import com.blog.exceptions.DeleteException;
import com.blog.repository.PostRepository;
import com.blog.repository.ReportRepository;
import com.blog.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AdminService {
    private UserRepository userRepository;
    private PostRepository postRepository;
    private UsersServices usersServices;
    private ReportRepository reportRepository;

    public AdminService(
            ReportRepository reportRepository,
            UserRepository userRepository,
            PostRepository postRepository,
            UsersServices usersServices) {
        this.userRepository = userRepository;
        this.usersServices = usersServices;
        this.postRepository = postRepository;
        this.reportRepository = reportRepository;
    }

    public List<UsersAdmineResponse> getUsers() {
        List<User> users = userRepository.findAll();
        UsersRespons usersRespons;
        try {
            usersRespons = usersServices.getCurrentUser();
        } catch (Exception e) {
            throw new UserNotLoginException("admin not loggin");
        }
        List<UsersAdmineResponse> usersToDto = cnvToDto(users, usersRespons);
        return usersToDto;
    }

    private List<UsersAdmineResponse> cnvToDto(List<User> users, UsersRespons usersRespons) {
        List<UsersAdmineResponse> usersDto = new ArrayList<>();
        for (User user : users) {
            if (user.getUuid().equals(usersRespons.getUuid())) {
                continue;
            }
            UsersAdmineResponse usersAdmineResponse = new UsersAdmineResponse();
            usersAdmineResponse.setEmail(user.getEmail());
            usersAdmineResponse.setFirstName(user.getFirstName());
            usersAdmineResponse.setLastName(user.getLastName());
            usersAdmineResponse.setUsername(user.getUserName());
            usersAdmineResponse.setUuid(user.getUuid());
            usersDto.add(usersAdmineResponse);
        }
        return usersDto;
    }

    public UsersAdmineResponse getUser(String uuid) {
        User user = userRepository.findByUuid(uuid).orElseThrow(() -> {
            throw new UserNotFoundException("user not found for admin pannel");
        });
        UsersAdmineResponse usersAdmineResponse = new UsersAdmineResponse();
        usersAdmineResponse.setEmail(user.getEmail());
        usersAdmineResponse.setFirstName(user.getFirstName());
        usersAdmineResponse.setLastName(user.getLastName());
        usersAdmineResponse.setUsername(user.getUserName());
        usersAdmineResponse.setUuid(user.getUuid());
        usersAdmineResponse.setPost(user.getPosts());
        usersAdmineResponse.setMessage("user has fetched successfully");
        return usersAdmineResponse;
    }

    public UsersAdmineResponse deleteUser(String uuid) {
        userRepository.deleteByUuid(uuid);
        UsersRespons usersRespons;
        try {
            usersRespons = usersServices.getCurrentUser();
        } catch (Exception e) {
            throw new UserNotLoginException("admin not loggin");
        }
        if (uuid.equals(usersRespons.getUuid())) {
            throw new DeleteException("u can't delete youre own account");
        }
        UsersAdmineResponse usersAdmineResponse = new UsersAdmineResponse();
        usersAdmineResponse.setMessage("user has deleted successfully");
        return usersAdmineResponse;
    }

    public UsersAdmineResponse banUser(String uuid) {
        User user = userRepository.findByUuid(uuid).orElseThrow(() -> {
            throw new UserNotFoundException("user not found for admin pannel");
        });
        UsersRespons usersRespons;
        try {
            usersRespons = usersServices.getCurrentUser();
        } catch (Exception e) {
            throw new UserNotLoginException("admin not loggin");
        }
        if (uuid.equals(usersRespons.getUuid())) {
            throw new BanException("u can't ban youre own account");
        }
        user.setStatus("ban");
        userRepository.save(user);
        UsersAdmineResponse usersAdmineResponse = new UsersAdmineResponse();
        usersAdmineResponse.setMessage("user has banned successfully");
        return usersAdmineResponse;
    }

    public PostRes deletePost(String uuid) {
        postRepository.deleteByUuid(uuid);
        PostRes postRes = new PostRes();
        postRes.setMessage("post has deleted successfully");
        return postRes;
    }

    public PostRes hidePost(String uuid) {
        Post post = postRepository.findByUuid(uuid).orElseThrow(() -> {
            throw new PostNotFoundException("post not found for admin pannel");
        });
        post.setStatus("hide");
        postRepository.save(post);
        PostRes postRes = new PostRes();
        postRes.setMessage("post has hide successfully");
        return postRes;
    }

    public List<ReportsAdmineResponse> getPostsReports() {
        List<Report> report = reportRepository.findByReportedPostIdIsNotNull();
        List<ReportsAdmineResponse> reportDto = cnvReportToDto(report, "Post");
        return reportDto;
    }

    public List<ReportsAdmineResponse> getUsersReports() {
        List<Report> report = reportRepository.findByReportedUserIdIsNotNull();
        List<ReportsAdmineResponse> reportDto = cnvReportToDto(report, "User");
        return reportDto;
    }

    private List<ReportsAdmineResponse> cnvReportToDto(List<Report> reports, String type) {
        List<ReportsAdmineResponse> reportsDto = new ArrayList<>();
        for (Report report : reports) {
            ReportsAdmineResponse ReportsAdmineResponse = new ReportsAdmineResponse();
            ReportsAdmineResponse.setUuid(report.getUuid());
            ReportsAdmineResponse.setReporterUsername(report.getReporterId().getUserName());
            ReportsAdmineResponse.setReason(report.getReason());
            ReportsAdmineResponse.setTime(report.getTime());
            if (type.equals("Post")) {
                ReportsAdmineResponse.setPostOrUserUuid(report.getReportedPostId().getUuid());
            } else if (type.equals("User")) {
                ReportsAdmineResponse.setPostOrUserUuid(report.getReportedUserId().getUuid());
            }
            reportsDto.add(ReportsAdmineResponse);
        }
        return reportsDto;
    }
}

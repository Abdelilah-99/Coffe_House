package com.blog.service;

import com.blog.entity.User;
import com.blog.dto.PostRes;
import com.blog.dto.ReportsAdmineResponse;
import com.blog.dto.AdminStatisticsResponse;
import com.blog.dto.TopUserResponse;
import com.blog.dto.TopPostResponse;
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
import com.blog.repository.CommentRepository;
import com.blog.repository.FollowRepository;
import com.blog.repository.LikesRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AdminService {
    private UserRepository userRepository;
    private PostRepository postRepository;
    private UsersServices usersServices;
    private ReportRepository reportRepository;
    private CommentRepository commentRepository;
    private FollowRepository followRepository;
    private LikesRepository likesRepository;

    public AdminService(
            ReportRepository reportRepository,
            UserRepository userRepository,
            PostRepository postRepository,
            UsersServices usersServices,
            CommentRepository commentRepository,
            FollowRepository followRepository,
            LikesRepository likesRepository) {
        this.userRepository = userRepository;
        this.usersServices = usersServices;
        this.postRepository = postRepository;
        this.reportRepository = reportRepository;
        this.commentRepository = commentRepository;
        this.followRepository = followRepository;
        this.likesRepository = likesRepository;
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

    public List<PostRes> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        List<PostRes> postsDto = cnvPostsToDto(posts);
        return postsDto;
    }

    private List<PostRes> cnvPostsToDto(List<Post> posts) {
        List<PostRes> postsDto = new ArrayList<>();
        for (Post post : posts) {
            PostRes postRes = new PostRes();
            postRes.setPostUuid(post.getUuid());
            postRes.setUserUuid(post.getUser().getUuid());
            postRes.setUserName(post.getUser().getUserName());
            postRes.setTitle(post.getTitle());
            postRes.setContent(post.getContent().length() > 150 ? post.getContent().substring(0, 150) + "..." : post.getContent());
            postRes.setCreatedAt(post.getCreatedAt());
            postRes.setMediaPaths(post.getMediaPaths());
            postRes.setStatus(post.getStatus());
            postRes.setCommentCount(commentRepository.countByPost_uuid(post.getUuid()));
            postRes.setLikeCount(likesRepository.countByPost_uuid(post.getUuid()));
            postRes.setProfileImagePath(post.getUser().getProfileImagePath());
            postsDto.add(postRes);
        }
        return postsDto;
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
            usersAdmineResponse.setStatus(user.getStatus());
            System.out.println("usersAdmineResponse: =======================>>>" + usersAdmineResponse.getStatus());
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
        String status = user.getStatus();
        String msg = new String();
        System.out.println("status: ===========>>> " + status);
        if (status.equals("BAN")) {
            user.setStatus("ACTIVE");
            msg = "unbanned";
        } else if (status.equals("ACTIVE")) {
            user.setStatus("BAN");
            msg = "banned";
        }
        userRepository.save(user);
        UsersAdmineResponse usersAdmineResponse = new UsersAdmineResponse();
        usersAdmineResponse.setMessage(String.format("user has %s successfully", msg));
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
        String status = post.getStatus();
        String msg = new String();
        if (status.equals("EPOSED")) {
            post.setStatus("HIDE");
            msg = "hide";
        } else if (status.equals("HIDE")) {
            post.setStatus("EPOSED");
            msg = "exposed";
        }
        System.out.println("post status: =======================+> " + post.getStatus());

        postRepository.save(post);
        PostRes postRes = new PostRes();
        postRes.setMessage(String.format("post has %s successfully", msg));
        return postRes;
    }

    public List<ReportsAdmineResponse> getPostsReports() {
        List<Report> report = reportRepository.findByPostIsNotNull();
        List<ReportsAdmineResponse> reportDto = cnvReportToDto(report, "Post");
        return reportDto;
    }

    public List<ReportsAdmineResponse> getUsersReports() {
        List<Report> report = reportRepository.findByUserIsNotNull();
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
            ReportsAdmineResponse.setTime(report.getCreatedAt());
            if (type.equals("Post")) {
                ReportsAdmineResponse.setPostOrUserUuid(report.getPost().getUuid());
            } else if (type.equals("User")) {
                ReportsAdmineResponse.setPostOrUserUuid(report.getUser().getUuid());
            }
            reportsDto.add(ReportsAdmineResponse);
        }
        return reportsDto;
    }

    public AdminStatisticsResponse getOverallStatistics() {
        long totalUsers = userRepository.count();
        long totalPosts = postRepository.count();
        long totalComments = commentRepository.count();
        long totalReports = reportRepository.count();
        long totalFollows = followRepository.count();

        AdminStatisticsResponse statistics = new AdminStatisticsResponse();
        statistics.setTotalUsers(totalUsers);
        statistics.setTotalPosts(totalPosts);
        statistics.setTotalComments(totalComments);
        statistics.setTotalReports(totalReports);
        statistics.setTotalFollows(totalFollows);
        statistics.setMessage("Overall statistics retrieved successfully");

        return statistics;
    }

    public List<TopUserResponse> getUsersWithMostComments() {
        List<User> allUsers = userRepository.findAll();
        List<TopUserResponse> topUsers = new ArrayList<>();

        for (User user : allUsers) {
            long commentCount = commentRepository.countByUser(user);
            TopUserResponse topUser = new TopUserResponse();
            topUser.setUuid(user.getUuid());
            topUser.setUsername(user.getUserName());
            topUser.setFirstName(user.getFirstName());
            topUser.setLastName(user.getLastName());
            topUser.setEmail(user.getEmail());
            topUser.setCount(commentCount);
            topUser.setType("comments");
            topUsers.add(topUser);
        }
        topUsers.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));
        return topUsers.subList(0, Math.min(10, topUsers.size()));
    }

    public List<TopUserResponse> getUsersWithMostFollowers() {
        List<User> allUsers = userRepository.findAll();
        List<TopUserResponse> topUsers = new ArrayList<>();

        for (User user : allUsers) {
            long followerCount = followRepository.countByFollowingId(user.getId());
            TopUserResponse topUser = new TopUserResponse();
            topUser.setUuid(user.getUuid());
            topUser.setUsername(user.getUserName());
            topUser.setFirstName(user.getFirstName());
            topUser.setLastName(user.getLastName());
            topUser.setEmail(user.getEmail());
            topUser.setCount(followerCount);
            topUser.setType("followers");
            topUsers.add(topUser);
        }
        topUsers.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));
        return topUsers.subList(0, Math.min(10, topUsers.size()));
    }

    public List<TopUserResponse> getMostReportedUsers() {
        List<User> allUsers = userRepository.findAll();
        List<TopUserResponse> topUsers = new ArrayList<>();

        for (User user : allUsers) {
            long reportCount = reportRepository.countByUser(user);
            if (reportCount > 0) {
                TopUserResponse topUser = new TopUserResponse();
                topUser.setUuid(user.getUuid());
                topUser.setUsername(user.getUserName());
                topUser.setFirstName(user.getFirstName());
                topUser.setLastName(user.getLastName());
                topUser.setEmail(user.getEmail());
                topUser.setCount(reportCount);
                topUser.setType("reports");
                topUsers.add(topUser);
            }
        }
        topUsers.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));
        return topUsers.subList(0, Math.min(10, topUsers.size()));
    }

    public List<TopPostResponse> getPostsWithMostComments() {
        List<Post> allPosts = postRepository.findAll();
        List<TopPostResponse> topPosts = new ArrayList<>();

        for (Post post : allPosts) {
            long commentCount = commentRepository.countByPost_uuid(post.getUuid());
            if (commentCount > 0) {
                TopPostResponse topPost = new TopPostResponse();
                topPost.setUuid(post.getUuid());
                topPost.setTitle(post.getTitle());
                topPost.setContent(post.getContent().length() > 100 ? post.getContent().substring(0, 100) + "..."
                        : post.getContent());
                topPost.setAuthorUsername(post.getUser().getUserName());
                topPost.setAuthorUuid(post.getUser().getUuid());
                topPost.setCommentCount(commentCount);
                topPost.setMessage("Posts with most comments retrieved successfully");
                topPosts.add(topPost);
            }
        }
        topPosts.sort((a, b) -> Long.compare(b.getCommentCount(), a.getCommentCount()));
        return topPosts.subList(0, Math.min(10, topPosts.size()));
    }

    public List<TopPostResponse> getMostReportedPosts() {
        List<Post> allPosts = postRepository.findAll();
        List<TopPostResponse> topPosts = new ArrayList<>();

        for (Post post : allPosts) {
            long reportCount = reportRepository.countByPost(post);
            if (reportCount > 0) {
                TopPostResponse topPost = new TopPostResponse();
                topPost.setUuid(post.getUuid());
                topPost.setTitle(post.getTitle());
                topPost.setContent(post.getContent().length() > 100 ? post.getContent().substring(0, 100) + "..."
                        : post.getContent());
                topPost.setAuthorUsername(post.getUser().getUserName());
                topPost.setAuthorUuid(post.getUser().getUuid());
                topPost.setReportCount(reportCount);
                topPost.setMessage("Most reported posts retrieved successfully");
                topPosts.add(topPost);
            }
        }
        topPosts.sort((a, b) -> Long.compare(b.getReportCount(), a.getReportCount()));
        return topPosts.subList(0, Math.min(10, topPosts.size()));
    }

    public List<TopPostResponse> getPostsWithMostLikes() {
        List<Post> allPosts = postRepository.findAll();
        List<TopPostResponse> topPosts = new ArrayList<>();

        for (Post post : allPosts) {
            long likeCount = likesRepository.countByPost_uuid(post.getUuid());
            if (likeCount > 0) {
                TopPostResponse topPost = new TopPostResponse();
                topPost.setUuid(post.getUuid());
                topPost.setTitle(post.getTitle());
                topPost.setContent(post.getContent().length() > 100 ? post.getContent().substring(0, 100) + "..."
                        : post.getContent());
                topPost.setAuthorUsername(post.getUser().getUserName());
                topPost.setAuthorUuid(post.getUser().getUuid());
                topPost.setLikeCount(likeCount);
                topPost.setMessage("Posts with most likes retrieved successfully");
                topPosts.add(topPost);
            }
        }
        topPosts.sort((a, b) -> Long.compare(b.getLikeCount(), a.getLikeCount()));
        return topPosts.subList(0, Math.min(10, topPosts.size()));
    }
}

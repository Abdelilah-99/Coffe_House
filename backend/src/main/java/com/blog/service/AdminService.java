package com.blog.service;

import com.blog.entity.User;
import com.blog.dto.MediaDTO;
import com.blog.dto.PostRes;
import com.blog.dto.ReportsAdmineResponse;
import com.blog.dto.AdminStatisticsResponse;
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
import com.blog.repository.LikesRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.*;

@Service
public class AdminService {
    private UserRepository userRepository;
    private PostRepository postRepository;
    private UsersServices usersServices;
    private ReportRepository reportRepository;
    private CommentRepository commentRepository;
    private LikesRepository likesRepository;

    public AdminService(
            ReportRepository reportRepository,
            UserRepository userRepository,
            PostRepository postRepository,
            UsersServices usersServices,
            CommentRepository commentRepository,
            LikesRepository likesRepository) {
        this.userRepository = userRepository;
        this.usersServices = usersServices;
        this.postRepository = postRepository;
        this.reportRepository = reportRepository;
        this.commentRepository = commentRepository;
        this.likesRepository = likesRepository;
    }

    private List<MediaDTO> convertToMediaDTOs(List<String> mediaPaths) {
        List<MediaDTO> mediaDTOs = new ArrayList<>();
        for (String path : mediaPaths) {
            String type = getMediaType(path);
            mediaDTOs.add(new MediaDTO(path, type));
        }
        return mediaDTOs;
    }

    private String getMediaType(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "unknown";
        }
        String lowerCasePath = filePath.toLowerCase();
        if (lowerCasePath.endsWith(".jpg") || lowerCasePath.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerCasePath.endsWith(".png")) {
            return "image/png";
        } else if (lowerCasePath.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerCasePath.endsWith(".webp")) {
            return "image/webp";
        } else if (lowerCasePath.endsWith(".mp4")) {
            return "video/mp4";
        } else if (lowerCasePath.endsWith(".webm")) {
            return "video/webm";
        } else if (lowerCasePath.endsWith(".mov")) {
            return "video/mov";
        } else if (lowerCasePath.endsWith(".avi")) {
            return "video/avi";
        }
        return "unknown";
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
            postRes.setMediaPaths(convertToMediaDTOs(post.getMediaPaths()));
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
            usersAdmineResponse.setCreatedAt(user.getCreatedAt());
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
        postRepository.save(post);
        PostRes postRes = new PostRes();
        postRes.setMessage(String.format("post has %s successfully", msg));
        return postRes;
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
        long totalReports = reportRepository.count();

        AdminStatisticsResponse statistics = new AdminStatisticsResponse();
        statistics.setTotalUsers(totalUsers);
        statistics.setTotalPosts(totalPosts);
        statistics.setTotalReports(totalReports);
        statistics.setMessage("Overall statistics retrieved successfully");

        return statistics;
    }

    public record UserPage(List<UsersAdmineResponse> users, Long lastCreatedAt, String lastUuid) {
    }

    public record PostPage(List<PostRes> posts, Long lastTime, String lastUuid) {
    }

    public record ReportPage(List<ReportsAdmineResponse> reports, Long lastTime, String lastUuid) {
    }

    public UserPage getUsersPaginated(Long lastCreatedAt, String lastUuid) {
        UsersRespons usersRespons;
        try {
            usersRespons = usersServices.getCurrentUser();
        } catch (Exception e) {
            throw new UserNotLoginException("admin not logged in");
        }

        Pageable pageable = PageRequest.of(0, 10);

        if (lastCreatedAt == null) {
            lastCreatedAt = System.currentTimeMillis() + 1000;
        }

        Long lastId = null;
        if (lastUuid != null) {
            User user = userRepository.findByUuid(lastUuid).orElse(null);
            if (user != null) {
                lastId = user.getId();
            }
        }

        List<User> users = userRepository.findAllPaginated(lastCreatedAt, lastId, pageable);
        List<UsersAdmineResponse> usersDto = cnvToDto(users, usersRespons);

        String newLastUuid = users.isEmpty() ? null : users.get(users.size() - 1).getUuid();
        Long newLastCreatedAt = users.isEmpty() ? null : users.get(users.size() - 1).getCreatedAt();
        return new UserPage(usersDto, newLastCreatedAt, newLastUuid);
    }

    public PostPage getAllPostsPaginated(Long lastTime, String lastUuid) {
        Pageable pageable = PageRequest.of(0, 10);

        if (lastTime == null) {
            lastTime = System.currentTimeMillis() + 1000;
        }

        Long lastId = null;
        if (lastUuid != null) {
            Post post = postRepository.findByUuid(lastUuid).orElse(null);
            if (post != null) {
                lastId = post.getId();
            }
        }

        List<Post> posts = postRepository.findAllPaginated(lastTime, lastId, pageable);
        List<PostRes> postsDto = cnvPostsToDto(posts);

        String newLastUuid = posts.isEmpty() ? null : posts.get(posts.size() - 1).getUuid();
        Long newLastTime = posts.isEmpty() ? null : posts.get(posts.size() - 1).getCreatedAt();
        return new PostPage(postsDto, newLastTime, newLastUuid);
    }

    public ReportPage getPostsReportsPaginated(Long lastTime, String lastUuid) {
        Pageable pageable = PageRequest.of(0, 10);

        if (lastTime == null) {
            lastTime = System.currentTimeMillis() + 1000;
        }

        Long lastId = null;
        if (lastUuid != null) {
            try {
                lastId = Long.parseLong(lastUuid);
            } catch (NumberFormatException e) {
                lastId = null;
            }
        }

        List<Report> reports = reportRepository.findByPostIsNotNullPaginated(lastTime, lastId, pageable);
        List<ReportsAdmineResponse> reportsDto = cnvReportToDto(reports, "Post");

        String newLastUuid = reports.isEmpty() ? null : String.valueOf(reports.get(reports.size() - 1).getId());
        Long newLastTime = reports.isEmpty() ? null : reports.get(reports.size() - 1).getCreatedAt();
        return new ReportPage(reportsDto, newLastTime, newLastUuid);
    }

    public ReportPage getUsersReportsPaginated(Long lastTime, String lastUuid) {
        Pageable pageable = PageRequest.of(0, 10);

        if (lastTime == null) {
            lastTime = System.currentTimeMillis() + 1000;
        }

        Long lastId = null;
        if (lastUuid != null) {
            try {
                lastId = Long.parseLong(lastUuid);
            } catch (NumberFormatException e) {
                lastId = null;
            }
        }

        List<Report> reports = reportRepository.findByUserIsNotNullPaginated(lastTime, lastId, pageable);
        List<ReportsAdmineResponse> reportsDto = cnvReportToDto(reports, "User");

        String newLastUuid = reports.isEmpty() ? null : String.valueOf(reports.get(reports.size() - 1).getId());
        Long newLastTime = reports.isEmpty() ? null : reports.get(reports.size() - 1).getCreatedAt();
        return new ReportPage(reportsDto, newLastTime, newLastUuid);
    }
}

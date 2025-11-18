package com.blog.service;

import java.io.File;
import java.util.*;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.blog.dto.CreatePostReq;
import com.blog.dto.MediaDTO;
import com.blog.dto.PostRes;
import com.blog.dto.UsersRespons;
import com.blog.entity.Follow;
import com.blog.entity.Notification;
import com.blog.entity.Post;

import com.blog.entity.User;
import com.blog.exceptions.*;
import com.blog.repository.*;
import com.blog.security.FileValidationService;
import com.blog.security.InputSanitizationService;

import jakarta.transaction.Transactional;

import java.io.IOException;

@Service
public class PostService {
    private final UsersServices usersServices;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;
    private final NotifRepository notifRepository;
    private final FileValidationService fileValidationService;
    private final InputSanitizationService inputSanitizationService;

    PostService(UsersServices usersServices,
            UserRepository userRepository,
            PostRepository postRepository,
            CommentRepository commentRepository,
            LikesRepository likesRepository,
            NotifRepository notifRepository,
            FileValidationService fileValidationService,
            InputSanitizationService inputSanitizationService) {
        this.usersServices = usersServices;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likesRepository = likesRepository;
        this.notifRepository = notifRepository;
        this.fileValidationService = fileValidationService;
        this.inputSanitizationService = inputSanitizationService;
    }

    @Transactional
    public PostRes createPost(CreatePostReq req) {
        String sanitizedTitle = inputSanitizationService.sanitizeTitle(req.getTitle());
        String sanitizedContent = inputSanitizationService.sanitizeContent(req.getContent());

        if (sanitizedTitle.isEmpty()) {
            throw new TitleEmptyException("Title not found");
        }

        if (sanitizedContent.isEmpty()) {
            throw new ContentEmptyException("Content not found");
        }

        if (req.getMediaFiles() != null && req.getMediaFiles().length > 5) {
            throw new SecurityException("5 file maximum");
        }

        try {
            UsersRespons userRes = usersServices.getCurrentUser();
            User user = userRepository.findByUuid(userRes.getUuid())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            if (user.getStatus() != null && user.getStatus().equals("BAN")) {
                throw new UserBannedException("the user is banned from creating posts");
            }
            List<String> mediaPaths = new ArrayList<>();
            if (req.getMediaFiles() != null && req.getMediaFiles().length > 0) {
                for (MultipartFile mediaFile : req.getMediaFiles()) {
                    if (!mediaFile.isEmpty()) {
                        try {
                            fileValidationService.validateFile(mediaFile);
                            String mediaPath = saveMedia(mediaFile);
                            mediaPaths.add(mediaPath);
                        } catch (com.blog.exceptions.InvalidFormatException e) {
                            throw new RuntimeException("File validation failed: " + e.getMessage(), e);
                        }
                    }
                }
            }
            List<Follow> followers = user.getFollowers();
            long time = System.currentTimeMillis();
            Post newPost = new Post();
            newPost.setUser(user);
            newPost.setContent(sanitizedContent);
            newPost.setTitle(sanitizedTitle);
            newPost.setCreatedAt(time);
            newPost.setMediaPaths(mediaPaths);
            newPost.setStatus("EPOSED");
            postRepository.save(newPost);
            List<Notification> notifications = new ArrayList<>();
            for (Follow follow : followers) {
                Notification newNotif = new Notification();
                newNotif.setNotificatedUser(follow.getFollower().getUuid());
                newNotif.setIsRead(false);
                newNotif.setNotification(String.format("%s has create a post", user.getUserName()));
                newNotif.setPostOrProfileUuid(newPost.getUuid());
                newNotif.setNotificationOwner(user.getUuid());
                notifications.add(newNotif);
            }
            notifRepository.saveAll(notifications);
            return new PostRes(
                    newPost.getUuid(),
                    user.getUuid(),
                    user.getUserName(),
                    sanitizedContent,
                    sanitizedTitle,
                    "Post created successefully",
                    time,
                    convertToMediaDTOs(mediaPaths),
                    0,
                    0,
                    user.getProfileImagePath(),
                    "EXPOSED");
        } catch (Exception e) {
            throw new ErrSavingException(e.getMessage());
        }
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

    public String saveMedia(MultipartFile mediaFile) {
        try {
            String uploadDir = "uploads/posts/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String orgName = mediaFile.getOriginalFilename();
            if (orgName == null) {
                throw new RuntimeException("Failed to catch name media file: " + mediaFile.getOriginalFilename());
            }

            String sanitizedName = fileValidationService.sanitizeFilename(orgName);
            String ext = sanitizedName.substring(sanitizedName.lastIndexOf('.'));
            String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID() + ext.toLowerCase();
            String filePath = uploadDir + fileName;

            mediaFile.transferTo(new File(dir.getAbsolutePath() + "/" + fileName));
            return filePath;
        } catch (IOException e) {
            throw new ErrSavingException(String.format("Error file i/o: " + e.getMessage(), e));
        } catch (InvalidFormatException e) {
            throw new ErrSavingException(String.format("Invalid filename: " + e.getMessage(), e));
        }
    }

    public record PostPage(List<PostRes> posts, Long lastTime, String lastUuid) {
    }

    public PostPage getPosts(Long lastTime, String lastUuid) {
        UsersRespons userRes;
        try {
            userRes = usersServices.getCurrentUser();
        } catch (Exception e) {
            throw new UserNotLoginException("login please for getting posts");
        }
        User user = userRepository.findByUserName(userRes.getUsername()).orElseThrow(() -> {
            throw new UserNotFoundException("error user not found");
        });
        Pageable pageable = PageRequest.of(0, 5);

        if (lastTime == null) {
            lastTime = System.currentTimeMillis() + 1000;
        }
        Long lastId = null;
        if (lastUuid != null) {
            Post post = postRepository.findByUuid(lastUuid)
                    .orElseThrow(() -> new PostNotFoundException("post not found for id"));
            lastId = post.getId();
        }
        List<Post> posts = postRepository.findByPagination(lastTime, lastId, user.getId(), pageable);

        List<PostRes> listPostRes = new ArrayList<>();
        for (Post post : posts) {
            PostRes postRes = new PostRes();
            postRes.setPostUuid(post.getUuid());
            postRes.setUserUuid(post.getUser().getUuid());
            postRes.setUserName(post.getUser().getUserName());
            postRes.setContent(post.getContent());
            postRes.setTitle(post.getTitle());
            postRes.setMessage("list of post");
            postRes.setCreatedAt(post.getCreatedAt());
            postRes.setMediaPaths(convertToMediaDTOs(post.getMediaPaths()));
            postRes.setCommentCount(commentRepository.countByPost_uuid(post.getUuid()));
            postRes.setLikeCount(likesRepository.countByPost_uuid(post.getUuid()));
            postRes.setProfileImagePath(post.getUser().getProfileImagePath());
            postRes.setStatus(post.getStatus());
            listPostRes.add(postRes);
        }
        String newLastUuid = posts.isEmpty() ? null : posts.get(posts.size() - 1).getUuid();
        Long newLastTime = posts.isEmpty() ? null : posts.get(posts.size() - 1).getCreatedAt();
        return new PostPage(listPostRes, newLastTime, newLastUuid);
    }

    public PostPage getMyPosts(Long lastTime, String lastUuid) {
        UsersRespons userRes;
        try {
            userRes = usersServices.getCurrentUser();
        } catch (Exception e) {
            throw new UserNotLoginException("login please for getting posts");
        }
        User user = userRepository.findByUserName(userRes.getUsername()).orElseThrow(() -> {
            throw new UserNotFoundException("error user not found");
        });
        Pageable pageable = PageRequest.of(0, 5);

        if (lastTime == null) {
            lastTime = System.currentTimeMillis() + 1000;
        }
        Long lastId = null;
        if (lastUuid != null) {
            Post post = postRepository.findByUuid(lastUuid)
                    .orElseThrow(() -> new PostNotFoundException("post not found for id"));
            lastId = post.getId();
        }
        List<Post> posts = postRepository.findMyPostByPagination(lastTime, lastId, user.getId(), pageable);

        List<PostRes> listPostRes = new ArrayList<>();
        for (Post post : posts) {
            PostRes postRes = new PostRes();
            postRes.setPostUuid(post.getUuid());
            postRes.setUserUuid(post.getUser().getUuid());
            postRes.setUserName(post.getUser().getUserName());
            postRes.setContent(post.getContent());
            postRes.setTitle(post.getTitle());
            postRes.setMessage("list of post");
            postRes.setCreatedAt(post.getCreatedAt());
            postRes.setMediaPaths(convertToMediaDTOs(post.getMediaPaths()));
            postRes.setCommentCount(commentRepository.countByPost_uuid(post.getUuid()));
            postRes.setLikeCount(likesRepository.countByPost_uuid(post.getUuid()));
            postRes.setProfileImagePath(post.getUser().getProfileImagePath());
            postRes.setStatus(post.getStatus());
            listPostRes.add(postRes);
        }
        String newLastUuid = posts.isEmpty() ? null : posts.get(posts.size() - 1).getUuid();
        Long newLastTime = posts.isEmpty() ? null : posts.get(posts.size() - 1).getCreatedAt();
        return new PostPage(listPostRes, newLastTime, newLastUuid);
    }

    public Optional<PostRes> getPost(String uuid) {
        Optional<Post> postOpt = postRepository.findByUuid(uuid);
        if (postOpt.isEmpty()) {
            return Optional.empty();
        }
        Post post = postOpt.get();
        String status = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        if ("HIDE".equals(post.getStatus()) && !status.contains("ROLE_ADMIN")) {
            return Optional.empty();
        }

        return Optional.of(new PostRes(post.getUuid(),
                post.getUser().getUuid(),
                post.getUser().getUserName(),
                post.getContent(),
                post.getTitle(),
                "data extracted success",
                post.getCreatedAt(),
                convertToMediaDTOs(post.getMediaPaths()),
                commentRepository.countByPost_uuid(post.getUuid()),
                likesRepository.countByPost_uuid(post.getUuid()),
                post.getUser().getProfileImagePath(),
                post.getStatus()));
    }

    public PostPage getPostsByUserPaginated(String userUuid, Long lastTime, String lastUuid) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(0, 10);

        if (lastTime == null) {
            lastTime = System.currentTimeMillis() + 1000;
        }
        Long lastId = null;
        if (lastUuid != null) {
            Post post = postRepository.findByUuid(lastUuid)
                    .orElseThrow(() -> new PostNotFoundException("post not found for id"));
            lastId = post.getId();
        }

        List<Post> posts = postRepository.findByUserPaginated(user.getId(), lastTime, lastId, pageable);

        List<PostRes> listPostRes = new ArrayList<>();
        for (Post post : posts) {
            if ("HIDE".equals(post.getStatus())) {
                continue;
            }
            PostRes postRes = new PostRes();
            postRes.setPostUuid(post.getUuid());
            postRes.setUserUuid(post.getUser().getUuid());
            postRes.setUserName(post.getUser().getUserName());
            postRes.setContent(post.getContent());
            postRes.setTitle(post.getTitle());
            postRes.setMessage("list of user posts");
            postRes.setCreatedAt(post.getCreatedAt());
            postRes.setMediaPaths(convertToMediaDTOs(post.getMediaPaths()));
            postRes.setCommentCount(commentRepository.countByPost_uuid(post.getUuid()));
            postRes.setLikeCount(likesRepository.countByPost_uuid(post.getUuid()));
            postRes.setProfileImagePath(post.getUser().getProfileImagePath());
            postRes.setStatus(post.getStatus());
            listPostRes.add(postRes);
        }

        String newLastUuid = posts.isEmpty() ? null : posts.get(posts.size() - 1).getUuid();
        Long newLastTime = posts.isEmpty() ? null : posts.get(posts.size() - 1).getCreatedAt();
        return new PostPage(listPostRes, newLastTime, newLastUuid);
    }
}

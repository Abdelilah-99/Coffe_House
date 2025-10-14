package com.blog.service;

import java.io.File;
import java.util.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.blog.dto.CreatePostReq;
import com.blog.dto.PostRes;
import com.blog.dto.UsersRespons;
import com.blog.entity.Follow;
import com.blog.entity.Notification;
import com.blog.entity.Post;
import com.blog.entity.User;
import com.blog.exceptions.*;
import com.blog.repository.*;
import java.io.IOException;

@Service
public class PostService {
    private final UsersServices usersServices;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;
    private final NotifRepository notifRepository;

    PostService(UsersServices usersServices,
            UserRepository userRepository,
            PostRepository postRepository,
            CommentRepository commentRepository,
            LikesRepository likesRepository,
            NotifRepository notifRepository) {
        this.usersServices = usersServices;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likesRepository = likesRepository;
        this.notifRepository = notifRepository;
    }

    public PostRes createPost(CreatePostReq req) {
        if (req.getTitle().trim().isEmpty()) {
            throw new TitleEmptyException("Title not found");
        }
        if (req.getContent().trim().isEmpty()) {
            throw new ContentEmptyException("Content not found");
        }
        try {
            // System.out.printf("req.getTitle(): \n", req.getTitle());
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
                        String mediaPath = saveMedia(mediaFile);
                        mediaPaths.add(mediaPath);
                    }
                }
            }
            List<Follow> followers = user.getFollowers();
            long time = System.currentTimeMillis();
            // Post newPost = new Post(
            // req.getTitle(),
            // req.getContent(),
            // req.getMedia(),
            // user,
            // time);
            Post newPost = new Post();
            newPost.setUser(user);
            newPost.setContent(req.getContent());
            newPost.setTitle(req.getTitle());
            newPost.setCreatedAt(time);
            newPost.setMediaPaths(mediaPaths);
            newPost.setStatus("EPOSED");
            postRepository.save(newPost);
            List<Notification> notifications = new ArrayList<>();
            for (Follow follow : followers) {
                Notification newNotif = new Notification();
                newNotif.setNotificatedUser(follow.getFollower().getUuid());
                System.out.println("follow username: " + follow.getFollower().getId());
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
                    req.getContent(),
                    req.getTitle(),
                    "Post created successefully",
                    time,
                    mediaPaths,
                    0,
                    0,
                    user.getProfileImagePath(),
                    "EXPOSED");
        } catch (Exception e) {
            throw new ErrSavingException(String.format("Error saving post in DB: " + e.getMessage(), e));
        }
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
            String ext = orgName.substring(orgName.lastIndexOf('.'));
            String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID() + ext;
            String filePath = uploadDir + fileName;
            mediaFile.transferTo(new File(dir.getAbsolutePath() + "/" + fileName));
            return filePath;
        } catch (IOException e) {
            throw new ErrSavingException(String.format("Error file i/o: " + e.getMessage(), e));
        }
    }

    public record PostPage(List<PostRes> posts, Long lastTime, Long lastId) {
    }

    public PostPage getPosts(Long lastTime, Long lastId) {
        Pageable pageable = PageRequest.of(0, 5);

        if (lastTime == null) {
            lastTime = System.currentTimeMillis() + 1000;
        }

        List<Post> posts = postRepository.findByPagination(lastTime, lastId, pageable);

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
            postRes.setMediaPaths(post.getMediaPaths());
            postRes.setCommentCount(commentRepository.countByPost_uuid(post.getUuid()));
            postRes.setLikeCount(likesRepository.countByPost_uuid(post.getUuid()));
            postRes.setProfileImagePath(post.getUser().getProfileImagePath());
            postRes.setStatus(post.getStatus());
            listPostRes.add(postRes);
        }
        Long newLastId = posts.isEmpty() ? null : posts.get(posts.size() - 1).getId();
        Long newLastTime = posts.isEmpty() ? null : posts.get(posts.size() - 1).getCreatedAt();
        return new PostPage(listPostRes, newLastTime, newLastId);
    }

    public List<PostRes> displayAllPosts() {
        UsersRespons userRes;
        try {
            userRes = usersServices.getCurrentUser();
        } catch (Exception e) {
            throw new UserNotLoginException("login please for getting posts");
        }
        User user = userRepository.findByUserName(userRes.getUsername()).orElseThrow(() -> {
            throw new UserNotFoundException("error user not found");
        });
        List<Post> posts = postRepository.findPostsFromFollowedUsers(user.getId());
        List<PostRes> listPostRes = new ArrayList<>();
        for (Post post : posts) {
            System.err.printf("post id: %s\n", post.getId());
            System.err.printf("user id: %s\n", post.getUser().getId());

            listPostRes.add(new PostRes(post.getUuid(),
                    post.getUser().getUuid(),
                    post.getUser().getUserName(),
                    post.getContent(),
                    post.getTitle(),
                    "list of post",
                    post.getCreatedAt(),
                    post.getMediaPaths(),
                    commentRepository.countByPost_uuid(post.getUuid()),
                    likesRepository.countByPost_uuid(post.getUuid()),
                    post.getUser().getProfileImagePath(),
                    post.getStatus()));
            // System.err.println(listPostRes.get(0).getUserId());
        }
        return listPostRes;
    }

    public PostRes getPost(String uuid) {
        Post post = postRepository.findByUuid(uuid).orElseThrow(() -> new PostNotFoundException("post not found"));

        if ("HIDE".equals(post.getStatus())) {
            throw new PostNotFoundException("This post is not available");
        }

        return new PostRes(post.getUuid(),
                post.getUser().getUuid(),
                post.getUser().getUserName(),
                post.getContent(),
                post.getTitle(),
                "data extracted success",
                post.getCreatedAt(),
                post.getMediaPaths(),
                commentRepository.countByPost_uuid(post.getUuid()),
                likesRepository.countByPost_uuid(post.getUuid()),
                post.getUser().getProfileImagePath(),
                post.getStatus());
    }

    public List<PostRes> getPostsByUser(String userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        List<Post> posts = postRepository.findByUser(user);
        List<PostRes> listPostRes = new ArrayList<>();
        for (Post post : posts) {
            if ("HIDE".equals(post.getStatus())) {
                continue;
            }
            listPostRes.add(new PostRes(post.getUuid(),
                    post.getUser().getUuid(),
                    post.getUser().getUserName(),
                    post.getContent(),
                    post.getTitle(),
                    "list of user posts",
                    post.getCreatedAt(),
                    post.getMediaPaths(),
                    commentRepository.countByPost_uuid(post.getUuid()),
                    likesRepository.countByPost_uuid(post.getUuid()),
                    post.getUser().getProfileImagePath(),
                    post.getStatus()));
        }
        return listPostRes;
    }
}

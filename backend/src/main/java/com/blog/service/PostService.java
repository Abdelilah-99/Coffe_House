package com.blog.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.blog.dto.CreatePostReq;
import com.blog.dto.PostRes;
import com.blog.dto.UsersRespons;
import com.blog.entity.Post;
import com.blog.entity.User;
import com.blog.exceptions.*;
import com.blog.repository.*;
import java.io.IOException;

@Service
public class PostService {
    private UsersServices usersServices;
    private UserRepository userRepository;
    private PostRepository postRepository;

    PostService(UsersServices usersServices, UserRepository userRepository, PostRepository postRepository) {
        this.usersServices = usersServices;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
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
            User user = userRepository.findByUserName(userRes.getUsername())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            List<String> mediaPaths = new ArrayList<>();
            if (req.getMediaFiles() != null && req.getMediaFiles().length > 0) {
                for (MultipartFile mediaFile : req.getMediaFiles()) {
                    if (!mediaFile.isEmpty()) {
                        String mediaPath = saveMedia(mediaFile);
                        mediaPaths.add(mediaPath);
                    }
                }
            }
            String time = LocalDateTime.now().toString();
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
            newPost.setTimestamp(time);
            newPost.setMediaPaths(mediaPaths);
            postRepository.save(newPost);
            return new PostRes(
                    newPost.getId(),
                    user.getId(),
                    user.getUserName(),
                    req.getContent(),
                    req.getTitle(),
                    "Post created successefully",
                    time,
                    mediaPaths);
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
            throw new ErrSavingException(String.format("Error saving post in DB: " + e.getMessage(), e));
        } catch (IllegalStateException e) {
            throw new ErrSavingException(String.format("Error saving post in DB: " + e.getMessage(), e));
        }
    }

    public List<PostRes> displayAllPosts() {
        List<Post> posts = postRepository.findAll();
        List<PostRes> listPostRes = new ArrayList<>();
        for (Post post : posts) {
            System.err.printf("post id: %s\n", post.getId());
            System.err.printf("user id: %s\n", post.getUser().getId());
            listPostRes.add(new PostRes(post.getId(),
                    post.getUser().getId(),
                    post.getUser().getUserName(),
                    post.getContent(),
                    post.getTitle(),
                    "list of post",
                    post.getTimestamp(),
                    post.getMediaPaths()));
            System.err.println(listPostRes.get(0).getUserId());
        }
        return listPostRes;
    }
}

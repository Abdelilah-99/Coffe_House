package com.blog.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.blog.dto.CreatePostReq;
import com.blog.dto.CreatePostRes;
import com.blog.dto.UsersRespons;
import com.blog.entity.Post;
import com.blog.entity.User;
import com.blog.exceptions.ErrSavingException;
import com.blog.exceptions.UserNotFoundException;
import com.blog.repository.*;

import io.jsonwebtoken.io.IOException;

@Service
public class CreatePostService {
    private UsersServices usersServices;
    private UserRepository userRepository;
    private PostRepository postRepository;

    CreatePostService(UsersServices usersServices, UserRepository userRepository, PostRepository postRepository) {
        this.usersServices = usersServices;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public CreatePostRes createPost(CreatePostReq req) {
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
            return new CreatePostRes(user,
                    req.getContent(),
                    req.getTitle(),
                    "Post created successefully",
                    time);
        } catch (Exception e) {
            throw new ErrSavingException(String.format("Err saving post in db ", e));
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
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save media file: " + mediaFile.getOriginalFilename(), e);
        }
    }
}

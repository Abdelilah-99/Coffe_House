package com.blog.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.blog.dto.EditPostReq;
import com.blog.dto.PostRes;
import com.blog.repository.*;

import java.io.IOException;

import com.blog.entity.*;
import com.blog.exceptions.ErrSavingException;
import com.blog.exceptions.InvalidFormatException;
import com.blog.exceptions.PostNotFoundException;
import com.blog.security.FileValidationService;
import com.blog.security.InputSanitizationService;

@Service
public class EditPostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;
    private final FileValidationService fileValidationService;
    private final InputSanitizationService inputSanitizationService;

    EditPostService(PostRepository postRepository,
            CommentRepository commentRepository,
            LikesRepository likesRepository,
            FileValidationService fileValidationService,
            InputSanitizationService inputSanitizationService) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likesRepository = likesRepository;
        this.fileValidationService = fileValidationService;
        this.inputSanitizationService = inputSanitizationService;
    }

    public PostRes editPost(String uuid, EditPostReq req) {
        Post post = postRepository.findByUuid(uuid).orElseThrow(() -> new PostNotFoundException("Post not found"));

        if (req.getContent() != null) {
            String sanitizedContent = inputSanitizationService.sanitizeContent(req.getContent());
            post.setContent(sanitizedContent);
        }
        if (req.getTitle() != null) {
            String sanitizedTitle = inputSanitizationService.sanitizeTitle(req.getTitle());
            post.setTitle(sanitizedTitle);
        }
        List<String> oldPaths = post.getMediaPaths();
        List<String> updatedPaths = new ArrayList<>(req.getPathFiles());

        if (req.getMediaFiles() != null) {

            File dir = new File("uploads/posts/");
            for (MultipartFile mediaFile : req.getMediaFiles()) {
                try {
                    fileValidationService.validateFile(mediaFile);
                } catch (InvalidFormatException e) {
                    throw new RuntimeException("File validation failed: " + e.getMessage(), e);
                }

                String uploadDir = "uploads/posts/";
                String orgName = mediaFile.getOriginalFilename();
                if (orgName == null) {
                    throw new RuntimeException("Invalid media file: null name");
                }

                String sanitizedName;
                try {
                    sanitizedName = fileValidationService.sanitizeFilename(orgName);
                } catch (InvalidFormatException e) {
                    throw new RuntimeException("Invalid filename: " + e.getMessage(), e);
                }

                String ext = sanitizedName.substring(sanitizedName.lastIndexOf('.'));
                String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID() + ext.toLowerCase();
                String filePath = uploadDir + fileName;
                System.err.printf("=== check file ===\n");
                System.err.printf("filePath: %s\n", filePath);
                try {
                    mediaFile.transferTo(new File(dir.getAbsolutePath() + "/" + fileName));
                } catch (IOException e) {
                    throw new ErrSavingException(String.format("Error saving post in DB1: " + e.getMessage(), e));
                }
                updatedPaths.add(filePath);
            }
        }

        List<String> pathToRemove = new ArrayList<>();
        List<String> pathFromReq = req.getPathFiles();
        int max = Math.max(oldPaths.size(), pathFromReq.size());
        for (int i = 0; i < max; i++) {
            if (!pathFromReq.contains(oldPaths.get(i))) {
                pathToRemove.add(oldPaths.get(i));
            }
        }
        System.err.printf("old paths: %s\npath to remove: %s\nupdated Path: %s\npath From Req: %s\n",
                oldPaths,
                pathToRemove,
                updatedPaths,
                pathFromReq);
        // for (String oldPath : oldPaths) {
        // if (!updatedPaths.contains(oldPath)) {
        // pathToRemove.add(oldPath);
        // }
        // }
        for (String pathRemove : pathToRemove) {
            new File(pathRemove).delete();
        }
        post.setMediaPaths(updatedPaths);
        postRepository.save(post);
        return new PostRes(post.getUuid(),
                post.getUser().getUuid(),
                post.getUser().getUserName(),
                post.getContent(),
                post.getTitle(),
                "post updated!!",
                post.getCreatedAt(),
                post.getMediaPaths(),
                commentRepository.countByPost_uuid(post.getUuid()),
                likesRepository.countByPost_uuid(post.getUuid()),
                post.getUser().getProfileImagePath(),
                post.getStatus());
    }
}

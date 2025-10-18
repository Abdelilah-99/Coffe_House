package com.blog.service;

import com.blog.repository.*;
import com.blog.entity.*;
import com.blog.exceptions.UserNotFoundException;
import com.blog.exceptions.CreateCommentException;
import com.blog.exceptions.PostNotFoundException;
import com.blog.exceptions.UserBannedException;
import com.blog.dto.*;
import com.blog.security.InputSanitizationService;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UsersServices usersServices;
    private final UserRepository userRepository;
    private final InputSanitizationService inputSanitizationService;

    public CommentService(CommentRepository commentRepository,
            PostRepository postRepository,
            UsersServices usersServices,
            UserRepository userRepository,
            InputSanitizationService inputSanitizationService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.usersServices = usersServices;
        this.userRepository = userRepository;
        this.inputSanitizationService = inputSanitizationService;
    }

    public CommentPostRes createComment(String uuid, CommentPostReq req) {
        Post post = postRepository.findByUuid(uuid)
                .orElseThrow(() -> new PostNotFoundException("Post not found for comment"));
        try {
            UsersRespons userDetail = usersServices.getCurrentUser();
            User user = userRepository.findByUuid(userDetail.getUuid())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            if (user.getStatus() != null && user.getStatus().equals("BAN")) {
                throw new UserBannedException("the user is banned from creating posts");
            }

            String sanitizedComment = inputSanitizationService.sanitizeComment(req.getComment());

            if (sanitizedComment.isEmpty()) {
                return new CommentPostRes(post.getUuid(), user.getUuid(), sanitizedComment, "comment is empty");
            }

            Comment newComment = new Comment();
            newComment.setComment(sanitizedComment);
            newComment.setUser(user);
            newComment.setPost(post);
            newComment.setUserName(user.getUserName());
            newComment.setCreatedAt(System.currentTimeMillis());
            newComment.setImageProfile(user.getProfileImagePath());
            newComment.setUserUuid(user.getUuid());
            commentRepository.save(newComment);

            return new CommentPostRes(post.getUuid(), user.getUuid(), sanitizedComment,
                    "comment has been created successfully");
        } catch (Exception e) {
            throw new CreateCommentException("Error in creating comment: " + e.getMessage());
        }
    }

    public CommentRes getComment(String uuid) {
        Post post = postRepository.findByUuid(uuid)
                .orElseThrow(() -> new PostNotFoundException("Post not found for comment"));
        UsersRespons userDetail;
        try {
            userDetail = usersServices.getCurrentUser();
        } catch (Exception e) {
            throw new UserNotFoundException("user not found for comment retriving");
        }
        return new CommentRes(userDetail.getUsername(), post.getUuid(), post.getUser().getUuid(), post.getComments());
    }
}

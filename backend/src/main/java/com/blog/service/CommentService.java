package com.blog.service;

import com.blog.dto.CommentPostReq;
import com.blog.dto.UsersRespons;
import com.blog.repository.*;
import com.blog.entity.Post;
import com.blog.entity.Comment;
import com.blog.entity.User;
import com.blog.exceptions.UserNotFoundException;
import com.blog.exceptions.CreateCommentException;
import com.blog.exceptions.PostNotFoundException;
import com.blog.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private UsersServices usersServices;
    private UserRepository userRepository;

    public CommentService() {
    }

    @Autowired
    public CommentService(CommentRepository commentRepository,
            PostRepository postRepository,
            UsersServices usersServices,
            UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.usersServices = usersServices;
        this.userRepository = userRepository;
    }

    public CommentPostRes createComment(String uuid, CommentPostReq req) {
        Post post = postRepository.findByUuid(uuid)
                .orElseThrow(() -> new PostNotFoundException("Post not found for comment"));
        try {
            UsersRespons userDetail = usersServices.getCurrentUser();
            User user = userRepository.findByUuid(userDetail.getUuid())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            if (req.getComment().trim().isEmpty()) {
                return new CommentPostRes(post.getUuid(), user.getUuid(), req.getComment(), "comment is empty");
            }

            Comment newComment = new Comment();
            newComment.setComment(req.getComment());
            newComment.setUser(user);
            newComment.setPost(post);
            commentRepository.save(newComment);

            return new CommentPostRes(post.getUuid(), user.getUuid(), newComment.getComment(),
                    "comment has been created successfully");
        } catch (Exception e) {
            throw new CreateCommentException("Error in creating comment: " + e.getMessage());
        }
    }

    public CommentRes getComment(String uuid) {
        Post post = postRepository.findByUuid(uuid)
                .orElseThrow(() -> new PostNotFoundException("Post not found for comment"));
        return new CommentRes(post.getUuid(), post.getUser().getUuid(), post.getComments());
    }
}

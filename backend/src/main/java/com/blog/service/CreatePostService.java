package com.blog.service;

import org.springframework.stereotype.Service;

import com.blog.dto.CreatePostReq;
import com.blog.dto.CreatePostRes;
import com.blog.dto.RegisterResponse;
import com.blog.dto.UsersRespons;
import com.blog.entity.Post;
import com.blog.entity.User;
import com.blog.exceptions.ErrSavingException;
import com.blog.exceptions.UserNotFoundException;
import com.blog.repository.*;

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
            UsersRespons userRes = usersServices.getCurrentUser();
            User user = userRepository.findByUserName(userRes.getUsername())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            Post newPost = new Post(
                    req.getTitle(),
                    req.getContent(),
                    req.getMedia(),
                    user);
            postRepository.save(newPost);
            return new CreatePostRes(user, req.getContent(), req.getTitle(), "Post created successefully");
        } catch (Exception e) {
            throw new ErrSavingException(String.format("Err saving post in db ", e));
        }
    }
}

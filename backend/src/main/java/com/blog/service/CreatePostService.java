package com.blog.service;

import org.springframework.stereotype.Service;

import com.blog.dto.CreatePostReq;
import com.blog.dto.CreatePostRes;
import com.blog.dto.RegisterResponse;
import com.blog.entity.Post;
import com.blog.entity.User;
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
            String userName = usersServices.getCurrentUser().getUsername();
            User user = userRepository.findByUserName(userName).orElseThrow();
            Post newPost = new Post(
                    req.getTitle(),
                    req.getContent(),
                    req.getMedia(),
                    user);
            postRepository.save(newPost);
            return new CreatePostRes(user, req.getContent(), req.getTitle(), "Post created successefully");
        } catch (Exception e) {
            // TODO: handle exception
            return new CreatePostRes(null, null, null, "Post not created successefully");
        }
    }
}

package com.blog.service;

import com.blog.dto.*;
import com.blog.entity.*;
import com.blog.repository.*;
import com.blog.exceptions.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikePostService {
    private UsersServices usersServices;
    private LikesRepository likesRepository;
    private PostRepository postRepository;
    private UserRepository userRepository;

    LikePostService() {
    }

    @Autowired
    LikePostService(UsersServices usersServices,
            LikesRepository likesRepository,
            PostRepository postRepository,
            UserRepository userRepository) {
        this.usersServices = usersServices;
        this.likesRepository = likesRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public LikePostRes likeLogic(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("post not found for like"));
        try {
            UsersRespons userRes = usersServices.getCurrentUser();
            User user = userRepository.findById(userRes.getId())
                    .orElseThrow(() -> new UserNotFoundException("User not found for like"));
            boolean isThereLike = likesRepository.existsByUserIdAndPostId(user.getId(), post.getId());
            if (isThereLike == true) {
                likesRepository.deleteByUserIdAndPostId(user.getId(), post.getId());
            } else {
                Like likes = new Like();
                likes.setUser(user);
                likes.setPost(post);
                likesRepository.save(likes);
            }
            return new LikePostRes(user.getId(), post.getId(), likesRepository.countByPostId(post.getId()));
        } catch (Exception e) {
            throw new LikeException("err like: " + e.getMessage());
        }
    }
}

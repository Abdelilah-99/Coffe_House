package com.blog.service;

import java.util.Optional;
import com.blog.dto.*;
import com.blog.entity.*;
import com.blog.repository.*;
import com.blog.exceptions.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikePostService {
    private final UsersServices usersServices;
    private final LikesRepository likesRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    LikePostService(
            UsersServices usersServices,
            LikesRepository likesRepository,
            PostRepository postRepository,
            UserRepository userRepository) {
        this.usersServices = usersServices;
        this.likesRepository = likesRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Optional<LikePostRes> likeLogic(String uuid) {
        Optional<Post> postOpt = postRepository.findByUuid(uuid);
        if (postOpt.isEmpty()) {
            return Optional.empty();
        }
        Post post = postOpt.get();
        try {
            UsersRespons userRes = usersServices.getCurrentUser();
            User user = userRepository.findByUuid(userRes.getUuid())
                    .orElseThrow(() -> new UserNotFoundException("User not found for like"));
            // boolean existsByUser_uuidAndPost_uuid(UUID userUuid, UUID postUuid);

            boolean isThereLike = likesRepository.existsByUser_uuidAndPost_uuid(user.getUuid(), post.getUuid());
            if (isThereLike == true) {
                likesRepository.deleteByUserIdAndPostId(user.getId(), post.getId());
            } else {
                // Like likes = new Like();
                // likes.setUser(user);
                // likes.setPost(post);
                // likesRepository.save(likes);
                likesRepository.insertLike(user.getId(), post.getId());
            }
            return Optional.of(
                    new LikePostRes(user.getUuid(), post.getUuid(), likesRepository.countByPost_uuid(post.getUuid())));
        } catch (Exception e) {
            throw new LikeException("err like: " + e.getMessage());
        }
    }
}

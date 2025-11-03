package com.blog.service;

import java.io.File;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.blog.dto.PostRes;
import com.blog.dto.UsersRespons;
import com.blog.exceptions.UserNotLoginException;
import com.blog.entity.Post;
import com.blog.repository.PostRepository;

@Service
public class DeletePostService {
    private final PostRepository postRepository;
    private final UsersServices usersServices;

    DeletePostService(
            PostRepository postRepository,
            UsersServices usersServices) {
        this.postRepository = postRepository;
        this.usersServices = usersServices;
    }

    public Optional<PostRes> deletePost(String uuid) {
        UsersRespons user;
        try {
            user = usersServices.getCurrentUser();
        } catch (Exception e) {
            throw new UserNotLoginException("User not authenticated");
        }
        Optional<Post> postOpt = postRepository.findByUuid(uuid);
        if (postOpt.isEmpty()) {
            return Optional.empty();
        }
        Post post = postOpt.get();

        if (!post.getUser().getUuid().equals(user.getUuid())) {
            throw new SecurityException("You are not authorized to delete this post");
        }

        for (String pathRemove : post.getMediaPaths()) {
            new File(pathRemove).delete();
        }

        postRepository.deleteById(post.getId());
        return Optional.of(new PostRes(
                post.getUuid(),
                post.getUser().getUuid(),
                null,
                null,
                null,
                "post deleted succesfully",
                0,
                null,
                0,
                0,
                null,
                null));
    }
}

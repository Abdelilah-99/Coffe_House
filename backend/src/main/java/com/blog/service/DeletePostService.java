package com.blog.service;

import java.io.File;
import org.springframework.stereotype.Service;
import com.blog.dto.PostRes;
import com.blog.dto.UsersRespons;
import com.blog.exceptions.UserNotFoundException;
import com.blog.entity.Post;
import com.blog.exceptions.PostNotFoundException;
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

    public PostRes deletePost(String uuid) {
        UsersRespons user;
        try {
            user = usersServices.getCurrentUser();
        } catch (Exception e) {
            throw new UserNotFoundException("hiii");
        }
        Post post = postRepository.findByUuid(uuid)
                .orElseThrow(() -> new PostNotFoundException("post not found for deleting"));
        for (String pathRemove : post.getMediaPaths()) {
            new File(pathRemove).delete();
        }
        if (!post.getUser().getUuid().equals(user.getUuid())) {
            return new PostRes(
                    post.getUuid(),
                    post.getUser().getUuid(),
                    null,
                    null,
                    null,
                    "this is not ur post",
                    0,
                    null,
                    0,
                    0,
                    null,
                    null);
        }
        postRepository.deleteById(post.getId());
        return new PostRes(
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
                null);
    }
}

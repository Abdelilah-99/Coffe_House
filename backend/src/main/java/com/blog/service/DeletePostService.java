package com.blog.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blog.dto.PostRes;
import com.blog.entity.Post;
import com.blog.exceptions.PostNotFoundException;
import com.blog.repository.PostRepository;

@Service
public class DeletePostService {
    public PostRepository postRepository;

    DeletePostService() {
    }

    @Autowired
    DeletePostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostRes deletePost(String uuid) {
        Post post = postRepository.findByUuid(uuid)
                .orElseThrow(() -> new PostNotFoundException("post not found for deleting"));
        for (String pathRemove : post.getMediaPaths()) {
            new File(pathRemove).delete();
        }
        postRepository.deleteByUuid(uuid);
        return new PostRes(
                post.getUuid(),
                post.getUser().getUuid(),
                null,
                null,
                null,
                "post deleted succesfully",
                null,
                null,
                0,
                0);
    }
}

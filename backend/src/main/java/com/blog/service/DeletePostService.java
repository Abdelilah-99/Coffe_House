package com.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blog.dto.PostRes;
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

    public PostRes deletePost(long id) {
        postRepository.deleteById(id);
        return new PostRes(null,
                null,
                null,
                "post deleted succesfully",
                null,
                null);
    }
}

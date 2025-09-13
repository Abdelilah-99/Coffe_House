package com.blog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.dto.CreatePostReq;
import com.blog.dto.CreatePostRes;
import com.blog.service.CreatePostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final CreatePostService createPostService;
    // private final EditPostService editPostService;
    // private final DeletePostService deletePostService;

    PostController(CreatePostService createPostService/*
                                                       * ,
                                                       * EditPostService editPostService,
                                                       * DeletePostService deletePostService
                                                       */) {
        this.createPostService = createPostService;
        // this.editPostService = editPostService;
        // this.deletePostService = deletePostService;
    }

    @PostMapping("/create")
    public ResponseEntity<CreatePostRes> createPost(@RequestBody CreatePostReq req) {
        try {
            CreatePostRes res = createPostService.createPost(req);
            
        } catch (Exception e) {
            // TODO: handle exception

        }
    }

    // @PostMapping("/edit")
    // @PostMapping("/delete")
}

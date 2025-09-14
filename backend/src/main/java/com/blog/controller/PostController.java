package com.blog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<CreatePostRes> createPost(@RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "mediaFiles", required = false) MultipartFile[] mediaFiles) {
        CreatePostReq req = new CreatePostReq(content, title, mediaFiles);
        System.out.printf("req.getTitle(): \n", req.getTitle());

        CreatePostRes res = createPostService.createPost(req);
        return ResponseEntity.ok(res);
    }

    // @PostMapping("/edit")
    // @PostMapping("/delete")
}

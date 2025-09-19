package com.blog.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.blog.dto.*;
import com.blog.service.PostService;
import com.blog.service.CommentService;
import com.blog.service.DeletePostService;
import com.blog.service.EditPostService;
import com.blog.service.LikePostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final EditPostService editPostService;
    private final DeletePostService deletePostService;
    private final CommentService commentService;
    private final LikePostService likePostService;

    PostController(PostService postService,
            EditPostService editPostService,
            DeletePostService deletePostService,
            CommentService commentService,
            LikePostService likePostService) {
        this.postService = postService;
        this.editPostService = editPostService;
        this.deletePostService = deletePostService;
        this.commentService = commentService;
        this.likePostService = likePostService;

    }

    @GetMapping("/all")
    public ResponseEntity<List<PostRes>> displayPost() {
        List<PostRes> res = postService.displayAllPosts();
        return ResponseEntity.ok(res);
    }

    @PostMapping("/create")
    public ResponseEntity<PostRes> createPost(@RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "mediaFiles", required = false) MultipartFile[] mediaFiles) {
        CreatePostReq req = new CreatePostReq(content, title, mediaFiles);
        System.out.printf("req.getTitle(): \n", req.getTitle());

        PostRes res = postService.createPost(req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<PostRes> editPost(@PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "mediaFiles", required = false) MultipartFile[] mediaFiles,
            @RequestParam(value = "pathFiles", required = false) List<String> pathFiles) {
        System.err.printf("post_id: %s\n", id);
        EditPostReq req = new EditPostReq(content, title, mediaFiles, pathFiles);
        PostRes res = editPostService.editPost(id, req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<PostRes> deletePost(@PathVariable Long id) {
        PostRes res = deletePostService.deletePost(id);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/comment/create/{id}")
    public ResponseEntity<CommentPostRes> commentPost(@PathVariable long id, @RequestBody CommentPostReq req) {
        CommentPostRes res = commentService.createComment(id, req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/like/{id}")
    public ResponseEntity<LikePostRes> likePost(@PathVariable long id) {
        LikePostRes res = likePostService.likeLogic(id);
        return ResponseEntity.ok(res);
    }
}

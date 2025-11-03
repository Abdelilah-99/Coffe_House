package com.blog.controller;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.blog.dto.*;
import com.blog.service.PostService;
import com.blog.service.PostService.PostPage;
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

    // @GetMapping("/all")
    // public ResponseEntity<List<PostRes>> displayPost() {
    //     List<PostRes> res = postService.displayAllPosts();
    //     return ResponseEntity.ok(res);
    // }

    @GetMapping("/home/pages")
    public ResponseEntity<PostPage> getPostByPage(
            @RequestParam(value = "lastTime", required = false) Long lastTime,
            @RequestParam(value = "lastId", required = false) String lastUuid) {
        PostPage data = postService.getPosts(lastTime, lastUuid);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/me/pages")
    public ResponseEntity<PostPage> getMyPostByPage(
            @RequestParam(value = "lastTime", required = false) Long lastTime,
            @RequestParam(value = "lastId", required = false) String lastUuid) {
        PostPage data = postService.getMyPosts(lastTime, lastUuid);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/create")
    public ResponseEntity<PostRes> createPost(@RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "mediaFiles", required = false) MultipartFile[] mediaFiles) {
        CreatePostReq req = new CreatePostReq(content, title, mediaFiles);
        PostRes res = postService.createPost(req);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/postCard/{uuid}")
    public ResponseEntity<PostRes> showPost(@PathVariable String uuid) {
        return postService.getPost(uuid)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/edit/{uuid}")
    public ResponseEntity<PostRes> editPost(@PathVariable String uuid,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "mediaFiles", required = false) MultipartFile[] mediaFiles,
            @RequestParam(value = "pathFiles", required = false) List<String> pathFiles) {
        System.err.printf("post_id: %s\n", uuid);
        EditPostReq req = new EditPostReq(content, title, mediaFiles, pathFiles);
        return editPostService.editPost(uuid, req)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/delete/{uuid}")
    public ResponseEntity<PostRes> deletePost(@PathVariable String uuid) {
        return deletePostService.deletePost(uuid)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/comment/{uuid}")
    public ResponseEntity<CommentRes> getComment(@PathVariable String uuid) {
        return commentService.getComment(uuid)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/comment/create/{uuid}")
    public ResponseEntity<CommentPostRes> commentPost(@PathVariable String uuid, @RequestBody CommentPostReq req) {
        CommentPostRes res = commentService.createComment(uuid, req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/comment/delete/{uuid}")
    public ResponseEntity<CommentPostRes> deletecommentPost(@PathVariable String uuid) {
        return commentService.deleteComment(uuid)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/like/{uuid}")
    public ResponseEntity<LikePostRes> likePost(@PathVariable String uuid) {
        return likePostService.likeLogic(uuid)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // @GetMapping("/user/{userUuid}")
    // public ResponseEntity<List<PostRes>> getUserPosts(@PathVariable String userUuid) {
    //     List<PostRes> res = postService.getPostsByUser(userUuid);
    //     return ResponseEntity.ok(res);
    // }

    @GetMapping("/user/{userUuid}/pages")
    public ResponseEntity<PostPage> getUserPostsByPage(
            @PathVariable String userUuid,
            @RequestParam(value = "lastTime", required = false) Long lastTime,
            @RequestParam(value = "lastUuid", required = false) String lastUuid) {
        PostPage data = postService.getPostsByUserPaginated(userUuid, lastTime, lastUuid);
        return ResponseEntity.ok(data);
    }

}

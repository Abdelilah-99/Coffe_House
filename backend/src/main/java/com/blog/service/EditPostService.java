// package com.blog.service;

// import java.io.File;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.UUID;

// import org.springframework.web.multipart.MultipartFile;

// import com.blog.dto.EditPostReq;
// import com.blog.dto.EditPostRes;
// import com.blog.repository.*;

// import java.io.IOException;

// import com.blog.entity.*;
// import com.blog.exceptions.PostNotFoundException;

// public class EditPostService {
//     private PostRepository postRepository;

//     EditPostService() {
//     }

//     EditPostService(PostRepository postRepository) {
//         this.postRepository = postRepository;
//     }

//     public EditPostRes editPost(long postId, EditPostReq req) throws IOException {
//         Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found"));
//         if (req.getContent() != null)
//             post.setContent(req.getContent());
//         if (req.getTitle() != null)
//             post.setTitle(req.getTitle());
//         List<String> oldPaths = post.getMediaPaths();
//         List<String> updatedPaths = new ArrayList<>();

//         if (req.getMediaFiles() != null) {
//             for (MultipartFile mediaFile : req.getMediaFiles()) {
//                 String uploadDir = "uploads/posts/";
//                 String orgName = mediaFile.getOriginalFilename();
//                 if (orgName == null) {
//                     throw new RuntimeException("Invalid media file: null name");
//                 }
//                 String ext = orgName.substring(orgName.lastIndexOf('.'));
//                 String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID() + ext;
//                 String filePath = uploadDir + fileName;

//                 mediaFile.transferTo(new File(filePath));
//                 updatedPaths.add(filePath);
//             }
//         }

//         List<String> pathToRemove = new ArrayList<>(oldPaths);
//         pathToRemove.removeAll(updatedPaths);
//         // for (String oldPath : oldPaths) {
//         // if (!updatedPaths.contains(oldPath)) {
//         // pathToRemove.add(oldPath);
//         // }
//         // }
//         for (String pathRemove : pathToRemove) {
//             new File(pathRemove).delete();
//         }
//         post.setMediaPaths(updatedPaths);
//         postRepository.save(post);
//         return new EditPostRes(post.getTitle(), post.getContent(), "Post updated");
//     }
// }

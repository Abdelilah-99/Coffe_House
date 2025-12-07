package com.blog.service;

import com.blog.dto.CommentPostReq;
import com.blog.dto.CommentPostRes;
import com.blog.dto.CommentRes;
import com.blog.dto.UsersRespons;
import com.blog.entity.Comment;
import com.blog.entity.Post;
import com.blog.entity.User;
import com.blog.exceptions.*;
import com.blog.repository.CommentRepository;
import com.blog.repository.PostRepository;
import com.blog.repository.UserRepository;
import com.blog.security.InputSanitizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentService Tests")
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UsersServices usersServices;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InputSanitizationService inputSanitizationService;

    @InjectMocks
    private CommentService commentService;

    private User testUser;
    private Post testPost;
    private Comment testComment;
    private UsersRespons testUserRespons;
    private CommentPostReq commentRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUuid("user-uuid-123");
        testUser.setUserName("testuser");
        testUser.setEmail("test@example.com");
        testUser.setStatus("ACTIVE");
        testUser.setProfileImagePath("profile.jpg");

        testPost = new Post();
        testPost.setId(1L);
        testPost.setUuid("post-uuid-123");
        testPost.setTitle("Test Post");
        testPost.setContent("Test Content");
        testPost.setUser(testUser);
        testPost.setComments(new ArrayList<>());

        testComment = new Comment();
        testComment.setId(1L);
        testComment.setUuid("comment-uuid-123");
        testComment.setComment("Test comment");
        testComment.setUser(testUser);
        testComment.setPost(testPost);
        testComment.setUserName("testuser");
        testComment.setUserUuid("user-uuid-123");
        testComment.setCreatedAt(System.currentTimeMillis());

        testUserRespons = new UsersRespons();
        testUserRespons.setUuid("user-uuid-123");
        testUserRespons.setUsername("testuser");

        commentRequest = new CommentPostReq();
        commentRequest.setComment("This is a test comment");
    }

    @Test
    @DisplayName("Should create comment successfully")
    void testCreateComment_Success() throws Exception {
        // Arrange
        when(postRepository.findByUuid("post-uuid-123"))
                .thenReturn(Optional.of(testPost));
        when(usersServices.getCurrentUser()).thenReturn(testUserRespons);
        when(userRepository.findByUuid("user-uuid-123"))
                .thenReturn(Optional.of(testUser));
        when(inputSanitizationService.sanitizeComment("This is a test comment"))
                .thenReturn("This is a test comment");
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(testComment);

        // Act
        CommentPostRes result = commentService.createComment("post-uuid-123", commentRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPostUuid()).isEqualTo("post-uuid-123");
        assertThat(result.getUserUuid()).isEqualTo("user-uuid-123");
        assertThat(result.getComment()).isEqualTo("This is a test comment");
        assertThat(result.getMessage()).isEqualTo("comment has been created successfully");

        verify(commentRepository).save(any(Comment.class));
        verify(inputSanitizationService).sanitizeComment("This is a test comment");
    }

    @Test
    @DisplayName("Should throw PostNotFoundException when post doesn't exist")
    void testCreateComment_PostNotFound() {
        // Arrange
        when(postRepository.findByUuid("non-existent-post"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> commentService.createComment("non-existent-post", commentRequest))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessage("Post not found for comment");

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user doesn't exist")
    void testCreateComment_UserNotFound() throws Exception {
        // Arrange
        when(postRepository.findByUuid("post-uuid-123"))
                .thenReturn(Optional.of(testPost));
        when(usersServices.getCurrentUser()).thenReturn(testUserRespons);
        when(userRepository.findByUuid("user-uuid-123"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> commentService.createComment("post-uuid-123", commentRequest))
                .isInstanceOf(CreateCommentException.class)
                .hasMessageContaining("Error in creating comment");

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Should throw UserBannedException when user is banned")
    void testCreateComment_BannedUser() throws Exception {
        // Arrange
        testUser.setStatus("BAN");
        when(postRepository.findByUuid("post-uuid-123"))
                .thenReturn(Optional.of(testPost));
        when(usersServices.getCurrentUser()).thenReturn(testUserRespons);
        when(userRepository.findByUuid("user-uuid-123"))
                .thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThatThrownBy(() -> commentService.createComment("post-uuid-123", commentRequest))
                .isInstanceOf(CreateCommentException.class)
                .hasMessageContaining("Error in creating comment");

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Should return message when comment is empty after sanitization")
    void testCreateComment_EmptyComment() throws Exception {
        // Arrange
        when(postRepository.findByUuid("post-uuid-123"))
                .thenReturn(Optional.of(testPost));
        when(usersServices.getCurrentUser()).thenReturn(testUserRespons);
        when(userRepository.findByUuid("user-uuid-123"))
                .thenReturn(Optional.of(testUser));
        when(inputSanitizationService.sanitizeComment("This is a test comment"))
                .thenReturn("");

        // Act
        CommentPostRes result = commentService.createComment("post-uuid-123", commentRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("comment is empty");
        assertThat(result.getComment()).isEmpty();

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Should get comments for post successfully")
    void testGetComment_Success() throws Exception {
        // Arrange
        testPost.getComments().add(testComment);
        when(postRepository.findByUuid("post-uuid-123"))
                .thenReturn(Optional.of(testPost));
        when(usersServices.getCurrentUser()).thenReturn(testUserRespons);

        // Act
        Optional<CommentRes> result = commentService.getComment("post-uuid-123");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getPostUuid()).isEqualTo("post-uuid-123");
        assertThat(result.get().getUserName("testuser")).isEqualTo("testuser");
        assertThat(result.get().getComments()).hasSize(1);

        verify(postRepository).findByUuid("post-uuid-123");
    }

    @Test
    @DisplayName("Should return empty when post not found for getComment")
    void testGetComment_PostNotFound() {
        // Arrange
        when(postRepository.findByUuid("non-existent-post"))
                .thenReturn(Optional.empty());

        // Act
        Optional<CommentRes> result = commentService.getComment("non-existent-post");

        // Assert
        assertThat(result).isEmpty();
        verify(postRepository).findByUuid("non-existent-post");
    }

    @Test
    @DisplayName("Should throw UserNotLoginException when user not logged in for getComment")
    void testGetComment_UserNotLoggedIn() throws Exception {
        // Arrange
        when(postRepository.findByUuid("post-uuid-123"))
                .thenReturn(Optional.of(testPost));
        when(usersServices.getCurrentUser())
                .thenThrow(new RuntimeException("Not authenticated"));

        // Act & Assert
        assertThatThrownBy(() -> commentService.getComment("post-uuid-123"))
                .isInstanceOf(UserNotLoginException.class)
                .hasMessage("user not found for comment retriving");
    }

    @Test
    @DisplayName("Should delete comment successfully")
    void testDeleteComment_Success() throws Exception {
        // Arrange
        when(usersServices.getCurrentUser()).thenReturn(testUserRespons);
        when(commentRepository.findByUuid("comment-uuid-123"))
                .thenReturn(Optional.of(testComment));

        // Act
        Optional<CommentPostRes> result = commentService.deleteComment("comment-uuid-123");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getPostUuid()).isEqualTo("post-uuid-123");
        assertThat(result.get().getMessage()).isEqualTo("Comment deleted successfully");

        verify(commentRepository).deleteByUuid("comment-uuid-123");
    }

    @Test
    @DisplayName("Should return empty when comment not found for deletion")
    void testDeleteComment_CommentNotFound() throws Exception {
        // Arrange
        when(usersServices.getCurrentUser()).thenReturn(testUserRespons);
        when(commentRepository.findByUuid("non-existent-comment"))
                .thenReturn(Optional.empty());

        // Act
        Optional<CommentPostRes> result = commentService.deleteComment("non-existent-comment");

        // Assert
        assertThat(result).isEmpty();
        verify(commentRepository, never()).deleteByUuid(anyString());
    }

    @Test
    @DisplayName("Should throw SecurityException when trying to delete someone else's comment")
    void testDeleteComment_NotOwner() throws Exception {
        // Arrange
        testUserRespons.setUuid("different-user-uuid");
        when(usersServices.getCurrentUser()).thenReturn(testUserRespons);
        when(commentRepository.findByUuid("comment-uuid-123"))
                .thenReturn(Optional.of(testComment));

        // Act & Assert
        assertThatThrownBy(() -> commentService.deleteComment("comment-uuid-123"))
                .isInstanceOf(SecurityException.class)
                .hasMessage("not your comment");

        verify(commentRepository, never()).deleteByUuid(anyString());
    }

    @Test
    @DisplayName("Should throw UserNotLoginException when user not logged in for deleteComment")
    void testDeleteComment_UserNotLoggedIn() throws Exception {
        // Arrange
        when(usersServices.getCurrentUser())
                .thenThrow(new RuntimeException("Not authenticated"));

        // Act & Assert
        assertThatThrownBy(() -> commentService.deleteComment("comment-uuid-123"))
                .isInstanceOf(UserNotLoginException.class)
                .hasMessage("user not found for comment deleting");

        verify(commentRepository, never()).deleteByUuid(anyString());
    }
}


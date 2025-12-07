package com.blog.service;

import com.blog.dto.LikePostRes;
import com.blog.dto.UsersRespons;
import com.blog.entity.Post;
import com.blog.entity.User;
import com.blog.exceptions.LikeException;
import com.blog.exceptions.UserNotFoundException;
import com.blog.repository.LikesRepository;
import com.blog.repository.PostRepository;
import com.blog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LikePostService Tests")
class LikePostServiceTest {

    @Mock
    private UsersServices usersServices;

    @Mock
    private LikesRepository likesRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LikePostService likePostService;

    private User testUser;
    private Post testPost;
    private UsersRespons testUserRespons;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUuid("user-uuid-123");
        testUser.setUserName("testuser");
        testUser.setEmail("test@example.com");
        testUser.setStatus("ACTIVE");

        testPost = new Post();
        testPost.setId(1L);
        testPost.setUuid("post-uuid-123");
        testPost.setTitle("Test Post");
        testPost.setContent("Test Content");
        testPost.setUser(testUser);

        testUserRespons = new UsersRespons();
        testUserRespons.setUuid("user-uuid-123");
        testUserRespons.setUsername("testuser");
    }

    @Test
    @DisplayName("Should like a post successfully when not already liked")
    void testLikeLogic_LikePost() throws Exception {
        // Arrange
        when(postRepository.findByUuid("post-uuid-123"))
                .thenReturn(Optional.of(testPost));
        when(usersServices.getCurrentUser()).thenReturn(testUserRespons);
        when(userRepository.findByUuid("user-uuid-123"))
                .thenReturn(Optional.of(testUser));
        when(likesRepository.existsByUser_uuidAndPost_uuid("user-uuid-123", "post-uuid-123"))
                .thenReturn(false);
        when(likesRepository.countByPost_uuid("post-uuid-123"))
                .thenReturn(1L);

        // Act
        Optional<LikePostRes> result = likePostService.likeLogic("post-uuid-123");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getUserUuid()).isEqualTo("user-uuid-123");
        assertThat(result.get().getPostUuid()).isEqualTo("post-uuid-123");
        assertThat(result.get().getLikeCount()).isEqualTo(1);

        verify(likesRepository).insertLike(1L, 1L);
        verify(likesRepository, never()).deleteByUserIdAndPostId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Should unlike a post successfully when already liked")
    void testLikeLogic_UnlikePost() throws Exception {
        // Arrange
        when(postRepository.findByUuid("post-uuid-123"))
                .thenReturn(Optional.of(testPost));
        when(usersServices.getCurrentUser()).thenReturn(testUserRespons);
        when(userRepository.findByUuid("user-uuid-123"))
                .thenReturn(Optional.of(testUser));
        when(likesRepository.existsByUser_uuidAndPost_uuid("user-uuid-123", "post-uuid-123"))
                .thenReturn(true);
        when(likesRepository.countByPost_uuid("post-uuid-123"))
                .thenReturn(0L);

        // Act
        Optional<LikePostRes> result = likePostService.likeLogic("post-uuid-123");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getUserUuid()).isEqualTo("user-uuid-123");
        assertThat(result.get().getPostUuid()).isEqualTo("post-uuid-123");
        assertThat(result.get().getLikeCount()).isEqualTo(0);

        verify(likesRepository).deleteByUserIdAndPostId(1L, 1L);
        verify(likesRepository, never()).insertLike(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Should return empty when post not found")
    void testLikeLogic_PostNotFound() {
        // Arrange
        when(postRepository.findByUuid("non-existent-post"))
                .thenReturn(Optional.empty());

        // Act
        Optional<LikePostRes> result = likePostService.likeLogic("non-existent-post");

        // Assert
        assertThat(result).isEmpty();
        verify(likesRepository, never()).insertLike(anyLong(), anyLong());
        verify(likesRepository, never()).deleteByUserIdAndPostId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user doesn't exist")
    void testLikeLogic_UserNotFound() throws Exception {
        // Arrange
        when(postRepository.findByUuid("post-uuid-123"))
                .thenReturn(Optional.of(testPost));
        when(usersServices.getCurrentUser()).thenReturn(testUserRespons);
        when(userRepository.findByUuid("user-uuid-123"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> likePostService.likeLogic("post-uuid-123"))
                .isInstanceOf(LikeException.class)
                .hasMessageContaining("err like");

        verify(likesRepository, never()).insertLike(anyLong(), anyLong());
        verify(likesRepository, never()).deleteByUserIdAndPostId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Should throw LikeException when getCurrentUser fails")
    void testLikeLogic_UserNotAuthenticated() throws Exception {
        // Arrange
        when(postRepository.findByUuid("post-uuid-123"))
                .thenReturn(Optional.of(testPost));
        when(usersServices.getCurrentUser())
                .thenThrow(new RuntimeException("Not authenticated"));

        // Act & Assert
        assertThatThrownBy(() -> likePostService.likeLogic("post-uuid-123"))
                .isInstanceOf(LikeException.class)
                .hasMessageContaining("err like");

        verify(likesRepository, never()).insertLike(anyLong(), anyLong());
        verify(likesRepository, never()).deleteByUserIdAndPostId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Should handle multiple likes and unlikes correctly")
    void testLikeLogic_MultipleLikesUnlikes() throws Exception {
        // Arrange - First like
        when(postRepository.findByUuid("post-uuid-123"))
                .thenReturn(Optional.of(testPost));
        when(usersServices.getCurrentUser()).thenReturn(testUserRespons);
        when(userRepository.findByUuid("user-uuid-123"))
                .thenReturn(Optional.of(testUser));
        when(likesRepository.existsByUser_uuidAndPost_uuid("user-uuid-123", "post-uuid-123"))
                .thenReturn(false)
                .thenReturn(true)
                .thenReturn(false);
        when(likesRepository.countByPost_uuid("post-uuid-123"))
                .thenReturn(1L)
                .thenReturn(0L)
                .thenReturn(1L);

        // Act & Assert - First like
        Optional<LikePostRes> result1 = likePostService.likeLogic("post-uuid-123");
        assertThat(result1).isPresent();
        assertThat(result1.get().getLikeCount()).isEqualTo(1);
        verify(likesRepository, times(1)).insertLike(1L, 1L);

        // Act & Assert - Unlike
        Optional<LikePostRes> result2 = likePostService.likeLogic("post-uuid-123");
        assertThat(result2).isPresent();
        assertThat(result2.get().getLikeCount()).isEqualTo(0);
        verify(likesRepository, times(1)).deleteByUserIdAndPostId(1L, 1L);

        // Act & Assert - Like again
        Optional<LikePostRes> result3 = likePostService.likeLogic("post-uuid-123");
        assertThat(result3).isPresent();
        assertThat(result3.get().getLikeCount()).isEqualTo(1);
        verify(likesRepository, times(2)).insertLike(1L, 1L);
    }
}


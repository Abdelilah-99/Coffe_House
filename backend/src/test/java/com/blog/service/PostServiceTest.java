package com.blog.service;

import com.blog.dto.CreatePostReq;
import com.blog.dto.MediaDTO;
import com.blog.dto.PostRes;
import com.blog.dto.UsersRespons;
import com.blog.entity.Follow;
import com.blog.entity.Post;
import com.blog.entity.User;
import com.blog.exceptions.*;
import com.blog.repository.*;
import com.blog.security.FileValidationService;
import com.blog.security.InputSanitizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService Tests")
class PostServiceTest {

    @Mock
    private UsersServices usersServices;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikesRepository likesRepository;

    @Mock
    private NotifRepository notifRepository;

    @Mock
    private FileValidationService fileValidationService;

    @Mock
    private InputSanitizationService inputSanitizationService;

    @InjectMocks
    private PostService postService;

    private User testUser;
    private CreatePostReq createPostReq;
    private UsersRespons usersRespons;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUuid("user-uuid-123");
        testUser.setUserName("testuser");
        testUser.setEmail("test@example.com");
        testUser.setStatus("ACTIVE");
        testUser.setFollowers(new ArrayList<>());

        usersRespons = new UsersRespons();
        usersRespons.setUuid("user-uuid-123");
        usersRespons.setUsername("testuser");

        createPostReq = new CreatePostReq();
        createPostReq.setTitle("Test Post Title");
        createPostReq.setContent("Test Post Content");
    }

    @Test
    @DisplayName("Should create post successfully without media")
    void testCreatePost_Success_NoMedia() throws Exception {
        // Arrange
        when(inputSanitizationService.sanitizeTitle(createPostReq.getTitle()))
                .thenReturn("Test Post Title");
        when(inputSanitizationService.sanitizeContent(createPostReq.getContent()))
                .thenReturn("Test Post Content");
        when(usersServices.getCurrentUser()).thenReturn(usersRespons);
        when(userRepository.findByUuid(usersRespons.getUuid()))
                .thenReturn(Optional.of(testUser));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post post = invocation.getArgument(0);
            post.setUuid("post-uuid-123");
            return post;
        });

        // Act
        PostRes result = postService.createPost(createPostReq);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Post Title");
        assertThat(result.getContent()).isEqualTo("Test Post Content");
        assertThat(result.getUserName()).isEqualTo("testuser");
        assertThat(result.getMessage()).isEqualTo("Post created successefully");
        assertThat(result.getStatus()).isEqualTo("EXPOSED");

        verify(postRepository).save(any(Post.class));
        verify(inputSanitizationService).sanitizeTitle(createPostReq.getTitle());
        verify(inputSanitizationService).sanitizeContent(createPostReq.getContent());
    }

    @Test
    @DisplayName("Should throw TitleEmptyException when title is empty after sanitization")
    void testCreatePost_EmptyTitle() {
        // Arrange
        when(inputSanitizationService.sanitizeTitle(createPostReq.getTitle()))
                .thenReturn("");

        // Act & Assert
        assertThatThrownBy(() -> postService.createPost(createPostReq))
                .isInstanceOf(TitleEmptyException.class)
                .hasMessage("Title not found");

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Should throw ContentEmptyException when content is empty after sanitization")
    void testCreatePost_EmptyContent() {
        // Arrange
        when(inputSanitizationService.sanitizeTitle(createPostReq.getTitle()))
                .thenReturn("Test Post Title");
        when(inputSanitizationService.sanitizeContent(createPostReq.getContent()))
                .thenReturn("");

        // Act & Assert
        assertThatThrownBy(() -> postService.createPost(createPostReq))
                .isInstanceOf(ContentEmptyException.class)
                .hasMessage("Content not found");

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user doesn't exist")
    void testCreatePost_UserNotFound() throws Exception {
        // Arrange
        when(inputSanitizationService.sanitizeTitle(createPostReq.getTitle()))
                .thenReturn("Test Post Title");
        when(inputSanitizationService.sanitizeContent(createPostReq.getContent()))
                .thenReturn("Test Post Content");
        when(usersServices.getCurrentUser()).thenReturn(usersRespons);
        when(userRepository.findByUuid(usersRespons.getUuid()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> postService.createPost(createPostReq))
                .isInstanceOf(ErrSavingException.class);

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Should throw UserBannedException when user is banned")
    void testCreatePost_BannedUser() throws Exception {
        // Arrange
        testUser.setStatus("BAN");
        when(inputSanitizationService.sanitizeTitle(createPostReq.getTitle()))
                .thenReturn("Test Post Title");
        when(inputSanitizationService.sanitizeContent(createPostReq.getContent()))
                .thenReturn("Test Post Content");
        when(usersServices.getCurrentUser()).thenReturn(usersRespons);
        when(userRepository.findByUuid(usersRespons.getUuid()))
                .thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThatThrownBy(() -> postService.createPost(createPostReq))
                .isInstanceOf(ErrSavingException.class);

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Should throw SecurityException when more than 5 media files")
    void testCreatePost_TooManyFiles() {
        // Arrange
        MultipartFile[] files = new MultipartFile[6];
        createPostReq.setMediaFiles(files);
        when(inputSanitizationService.sanitizeTitle(createPostReq.getTitle()))
                .thenReturn("Test Post Title");
        when(inputSanitizationService.sanitizeContent(createPostReq.getContent()))
                .thenReturn("Test Post Content");

        // Act & Assert
        assertThatThrownBy(() -> postService.createPost(createPostReq))
                .isInstanceOf(SecurityException.class)
                .hasMessage("5 file maximum");

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Should get post by UUID successfully")
    void testGetPost_Success() {
        // Arrange
        Post testPost = new Post();
        testPost.setUuid("post-uuid-123");
        testPost.setTitle("Test Title");
        testPost.setContent("Test Content");
        testPost.setCreatedAt(System.currentTimeMillis());
        testPost.setStatus("EXPOSED");
        testPost.setUser(testUser);
        testPost.setMediaPaths(new ArrayList<>());

        when(postRepository.findByUuid("post-uuid-123"))
                .thenReturn(Optional.of(testPost));
        when(commentRepository.countByPost_uuid("post-uuid-123")).thenReturn(5L);
        when(likesRepository.countByPost_uuid("post-uuid-123")).thenReturn(10L);

        // Act
        Optional<PostRes> result = postService.getPost("post-uuid-123");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getPostUuid()).isEqualTo("post-uuid-123");
        assertThat(result.get().getTitle()).isEqualTo("Test Title");
        assertThat(result.get().getContent()).isEqualTo("Test Content");
        assertThat(result.get().getCommentCount()).isEqualTo(5);
        assertThat(result.get().getLikeCount()).isEqualTo(10);

        verify(postRepository).findByUuid("post-uuid-123");
    }

    @Test
    @DisplayName("Should return empty when post not found")
    void testGetPost_NotFound() {
        // Arrange
        when(postRepository.findByUuid("non-existent-uuid"))
                .thenReturn(Optional.empty());

        // Act
        Optional<PostRes> result = postService.getPost("non-existent-uuid");

        // Assert
        assertThat(result).isEmpty();
        verify(postRepository).findByUuid("non-existent-uuid");
    }

    @Test
    @DisplayName("Should get posts with pagination")
    void testGetPosts_Success() throws Exception {
        // Arrange
        List<Post> mockPosts = new ArrayList<>();
        Post post1 = new Post();
        post1.setId(1L);
        post1.setUuid("post-1");
        post1.setTitle("Title 1");
        post1.setContent("Content 1");
        post1.setCreatedAt(System.currentTimeMillis());
        post1.setUser(testUser);
        post1.setMediaPaths(new ArrayList<>());
        post1.setStatus("EXPOSED");
        mockPosts.add(post1);

        when(usersServices.getCurrentUser()).thenReturn(usersRespons);
        when(userRepository.findByUserName(usersRespons.getUsername()))
                .thenReturn(Optional.of(testUser));
        when(postRepository.findByPagination(anyLong(), any(), anyLong(), any(Pageable.class)))
                .thenReturn(mockPosts);
        when(commentRepository.countByPost_uuid(anyString())).thenReturn(0L);
        when(likesRepository.countByPost_uuid(anyString())).thenReturn(0L);

        // Act
        PostService.PostPage result = postService.getPosts(null, null);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.posts()).hasSize(1);
        assertThat(result.posts().get(0).getPostUuid()).isEqualTo("post-1");
        assertThat(result.lastUuid()).isEqualTo("post-1");

        verify(postRepository).findByPagination(anyLong(), any(), anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("Should throw UserNotLoginException when user not logged in for getPosts")
    void testGetPosts_UserNotLoggedIn() throws Exception {
        // Arrange
        when(usersServices.getCurrentUser())
                .thenThrow(new RuntimeException("Not authenticated"));

        // Act & Assert
        assertThatThrownBy(() -> postService.getPosts(null, null))
                .isInstanceOf(UserNotLoginException.class)
                .hasMessage("login please for getting posts");
    }

    @Test
    @DisplayName("Should get my posts successfully")
    void testGetMyPosts_Success() throws Exception {
        // Arrange
        List<Post> mockPosts = new ArrayList<>();
        Post post1 = new Post();
        post1.setId(1L);
        post1.setUuid("my-post-1");
        post1.setTitle("My Title 1");
        post1.setContent("My Content 1");
        post1.setCreatedAt(System.currentTimeMillis());
        post1.setUser(testUser);
        post1.setMediaPaths(new ArrayList<>());
        post1.setStatus("EXPOSED");
        mockPosts.add(post1);

        when(usersServices.getCurrentUser()).thenReturn(usersRespons);
        when(userRepository.findByUserName(usersRespons.getUsername()))
                .thenReturn(Optional.of(testUser));
        when(postRepository.findMyPostByPagination(anyLong(), any(), anyLong(), any(Pageable.class)))
                .thenReturn(mockPosts);
        when(commentRepository.countByPost_uuid(anyString())).thenReturn(0L);
        when(likesRepository.countByPost_uuid(anyString())).thenReturn(0L);

        // Act
        PostService.PostPage result = postService.getMyPosts(null, null);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.posts()).hasSize(1);
        assertThat(result.posts().get(0).getPostUuid()).isEqualTo("my-post-1");

        verify(postRepository).findMyPostByPagination(anyLong(), any(), anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("Should get posts by user UUID paginated")
    void testGetPostsByUserPaginated_Success() {
        // Arrange
        List<Post> mockPosts = new ArrayList<>();
        Post post1 = new Post();
        post1.setId(1L);
        post1.setUuid("user-post-1");
        post1.setTitle("User Title 1");
        post1.setContent("User Content 1");
        post1.setCreatedAt(System.currentTimeMillis());
        post1.setUser(testUser);
        post1.setMediaPaths(new ArrayList<>());
        post1.setStatus("EXPOSED");
        mockPosts.add(post1);

        when(userRepository.findByUuid("user-uuid-123"))
                .thenReturn(Optional.of(testUser));
        when(postRepository.findByUserPaginated(anyLong(), anyLong(), any(), any(Pageable.class)))
                .thenReturn(mockPosts);
        when(commentRepository.countByPost_uuid(anyString())).thenReturn(0L);
        when(likesRepository.countByPost_uuid(anyString())).thenReturn(0L);

        // Act
        PostService.PostPage result = postService.getPostsByUserPaginated("user-uuid-123", null, null);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.posts()).hasSize(1);
        assertThat(result.posts().get(0).getPostUuid()).isEqualTo("user-post-1");

        verify(userRepository).findByUuid("user-uuid-123");
    }

    @Test
    @DisplayName("Should filter out hidden posts in user posts")
    void testGetPostsByUserPaginated_FilterHiddenPosts() {
        // Arrange
        List<Post> mockPosts = new ArrayList<>();
        
        Post post1 = new Post();
        post1.setId(1L);
        post1.setUuid("exposed-post");
        post1.setTitle("Exposed Post");
        post1.setContent("Content");
        post1.setCreatedAt(System.currentTimeMillis());
        post1.setUser(testUser);
        post1.setMediaPaths(new ArrayList<>());
        post1.setStatus("EXPOSED");
        mockPosts.add(post1);

        Post post2 = new Post();
        post2.setId(2L);
        post2.setUuid("hidden-post");
        post2.setTitle("Hidden Post");
        post2.setContent("Content");
        post2.setCreatedAt(System.currentTimeMillis());
        post2.setUser(testUser);
        post2.setMediaPaths(new ArrayList<>());
        post2.setStatus("HIDE");
        mockPosts.add(post2);

        when(userRepository.findByUuid("user-uuid-123"))
                .thenReturn(Optional.of(testUser));
        when(postRepository.findByUserPaginated(anyLong(), anyLong(), any(), any(Pageable.class)))
                .thenReturn(mockPosts);
        when(commentRepository.countByPost_uuid(anyString())).thenReturn(0L);
        when(likesRepository.countByPost_uuid(anyString())).thenReturn(0L);

        // Act
        PostService.PostPage result = postService.getPostsByUserPaginated("user-uuid-123", null, null);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.posts()).hasSize(1);
        assertThat(result.posts().get(0).getPostUuid()).isEqualTo("exposed-post");

        verify(userRepository).findByUuid("user-uuid-123");
    }
}



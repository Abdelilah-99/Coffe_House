package com.blog.service;

import com.blog.dto.UserFollowRes;
import com.blog.dto.UsersRespons;
import com.blog.dto.FollowUserResponse;
import com.blog.entity.Follow;
import com.blog.entity.User;
import com.blog.exceptions.*;
import com.blog.repository.FollowRepository;
import com.blog.repository.NotifRepository;
import com.blog.repository.UserRepository;
import com.blog.security.InputSanitizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsersServices Tests")
class UsersServicesTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private NotifRepository notifRepository;

    @Mock
    private PostService postService;

    @Mock
    private InputSanitizationService inputSanitizationService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private UsersServices usersServices;

    private User testUser;
    private User otherUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUuid("user-uuid-1");
        testUser.setUserName("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole("ROLE_USER");
        testUser.setStatus("ACTIVE");

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUuid("user-uuid-2");
        otherUser.setUserName("otheruser");
        otherUser.setEmail("other@example.com");
        otherUser.setFirstName("Other");
        otherUser.setLastName("User");
        otherUser.setRole("ROLE_USER");
        otherUser.setStatus("ACTIVE");
    }

    @Test
    @DisplayName("Should get all users successfully")
    void testFindAll_Success() {
        // Arrange
        List<User> users = Arrays.asList(testUser, otherUser);
        when(userRepository.findAll()).thenReturn(users);
        when(followRepository.countByFollowerId(anyLong())).thenReturn(5L);
        when(followRepository.countByFollowingId(anyLong())).thenReturn(10L);

        // Act
        List<UsersRespons> result = usersServices.findAll();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUsername()).isEqualTo("testuser");
        assertThat(result.get(1).getUsername()).isEqualTo("otheruser");

        verify(userRepository).findAll();
        verify(followRepository, times(2)).countByFollowerId(anyLong());
        verify(followRepository, times(2)).countByFollowingId(anyLong());
    }

    @Test
    @DisplayName("Should get current user successfully")
    void testGetCurrentUser_Success() throws Exception {
        // Arrange
        when(userDetails.getUsername()).thenReturn("testuser");
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUserName("testuser"))
                .thenReturn(Optional.of(testUser));
        when(followRepository.countByFollowerId(testUser.getId())).thenReturn(5L);
        when(followRepository.countByFollowingId(testUser.getId())).thenReturn(10L);

        // Act
        UsersRespons result = usersServices.getCurrentUser();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getFollower()).isEqualTo(5L);
        assertThat(result.getFollowing()).isEqualTo(10L);

        verify(userRepository).findByUserName("testuser");
    }

    @Test
    @DisplayName("Should throw exception when user not authenticated")
    void testGetCurrentUser_NotAuthenticated() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // Act & Assert
        assertThatThrownBy(() -> usersServices.getCurrentUser())
                .isInstanceOf(Exception.class)
                .hasMessage("User not authenticated");
    }

    @Test
    @DisplayName("Should follow user successfully")
    void testFollow_Success() throws Exception {
        // Arrange
        setupAuthentication();
        
        when(userRepository.findByUserName("testuser"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.findByUuid("user-uuid-2"))
                .thenReturn(Optional.of(otherUser));
        when(followRepository.existsByFollowerIdAndFollowingId(testUser.getId(), otherUser.getId()))
                .thenReturn(false);
        when(followRepository.countByFollowerId(otherUser.getId())).thenReturn(5L);
        when(followRepository.countByFollowingId(otherUser.getId())).thenReturn(10L);

        // Act
        Optional<UserFollowRes> result = usersServices.follow("user-uuid-2");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getMessage()).isEqualTo("user has succseffully followed");
        assertThat(result.get().getFollower()).isEqualTo(5L);
        assertThat(result.get().getFollowing()).isEqualTo(10L);

        verify(followRepository).insertFollow(testUser.getId(), otherUser.getId());
    }

    @Test
    @DisplayName("Should not follow if already following")
    void testFollow_AlreadyFollowing() throws Exception {
        // Arrange
        setupAuthentication();
        
        when(userRepository.findByUserName("testuser"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.findByUuid("user-uuid-2"))
                .thenReturn(Optional.of(otherUser));
        when(followRepository.existsByFollowerIdAndFollowingId(testUser.getId(), otherUser.getId()))
                .thenReturn(true);
        when(followRepository.countByFollowerId(otherUser.getId())).thenReturn(5L);
        when(followRepository.countByFollowingId(otherUser.getId())).thenReturn(10L);

        // Act
        Optional<UserFollowRes> result = usersServices.follow("user-uuid-2");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getMessage()).isEqualTo("already following");

        verify(followRepository, never()).insertFollow(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Should throw FollowException when trying to follow self")
    void testFollow_CannotFollowSelf() throws Exception {
        // Arrange
        setupAuthentication();
        
        when(userRepository.findByUserName("testuser"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.findByUuid("user-uuid-1"))
                .thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThatThrownBy(() -> usersServices.follow("user-uuid-1"))
                .isInstanceOf(FollowException.class)
                .hasMessage("you can't follow yourself");

        verify(followRepository, never()).insertFollow(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Should throw FollowException when trying to follow banned user")
    void testFollow_BannedUser() throws Exception {
        // Arrange
        setupAuthentication();
        otherUser.setStatus("BAN");
        
        when(userRepository.findByUserName("testuser"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.findByUuid("user-uuid-2"))
                .thenReturn(Optional.of(otherUser));

        // Act & Assert
        assertThatThrownBy(() -> usersServices.follow("user-uuid-2"))
                .isInstanceOf(FollowException.class)
                .hasMessage("This user is banned and cannot be followed");

        verify(followRepository, never()).insertFollow(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Should unfollow user successfully")
    void testUnfollow_Success() throws Exception {
        // Arrange
        setupAuthentication();
        
        when(userRepository.findByUserName("testuser"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.findByUuid("user-uuid-2"))
                .thenReturn(Optional.of(otherUser));
        when(followRepository.existsByFollowerIdAndFollowingId(testUser.getId(), otherUser.getId()))
                .thenReturn(true);
        when(followRepository.countByFollowerId(otherUser.getId())).thenReturn(4L);
        when(followRepository.countByFollowingId(otherUser.getId())).thenReturn(10L);

        // Act
        Optional<UserFollowRes> result = usersServices.unfollow("user-uuid-2");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getMessage()).isEqualTo("user has succseffully unfollowed");

        verify(followRepository).deleteByFollowerIdAndFollowingId(testUser.getId(), otherUser.getId());
        verify(notifRepository).deleteByNotificatedUserAndNotificationOwner(testUser.getUuid(), otherUser.getUuid());
    }

    @Test
    @DisplayName("Should check if following user")
    void testIsFollowing_True() throws Exception {
        // Arrange
        setupAuthentication();
        
        when(userRepository.findByUserName("testuser"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.findByUuid("user-uuid-2"))
                .thenReturn(Optional.of(otherUser));
        when(followRepository.existsByFollowerIdAndFollowingId(testUser.getId(), otherUser.getId()))
                .thenReturn(true);
        when(followRepository.countByFollowerId(testUser.getId())).thenReturn(5L);
        when(followRepository.countByFollowingId(testUser.getId())).thenReturn(10L);

        // Act
        Optional<UserFollowRes> result = usersServices.isFollowing("user-uuid-2");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getMessage()).isEqualTo("follow");

        verify(followRepository).existsByFollowerIdAndFollowingId(testUser.getId(), otherUser.getId());
    }

    @Test
    @DisplayName("Should check if not following user")
    void testIsFollowing_False() throws Exception {
        // Arrange
        setupAuthentication();
        
        when(userRepository.findByUserName("testuser"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.findByUuid("user-uuid-2"))
                .thenReturn(Optional.of(otherUser));
        when(followRepository.existsByFollowerIdAndFollowingId(testUser.getId(), otherUser.getId()))
                .thenReturn(false);
        when(followRepository.countByFollowerId(testUser.getId())).thenReturn(5L);
        when(followRepository.countByFollowingId(testUser.getId())).thenReturn(10L);

        // Act
        Optional<UserFollowRes> result = usersServices.isFollowing("user-uuid-2");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getMessage()).isEqualTo("no follow");
    }

    @Test
    @DisplayName("Should search users by username")
    void testGetUser_Success() throws Exception {
        // Arrange
        setupAuthentication();
        
        List<User> foundUsers = Collections.singletonList(otherUser);
        when(userRepository.findByUuidNotAndUserNameStartingWith("user-uuid-1", "other"))
                .thenReturn(foundUsers);

        // Act
        List<UsersRespons> result = usersServices.getUser("other");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("otheruser");

        verify(userRepository).findByUuidNotAndUserNameStartingWith("user-uuid-1", "other");
    }

    @Test
    @DisplayName("Should return empty list for empty username search")
    void testGetUser_EmptyUsername() throws Exception {
        // Arrange
        setupAuthentication();

        // Act
        List<UsersRespons> result = usersServices.getUser("");

        // Assert
        assertThat(result).isEmpty();

        verify(userRepository, never()).findByUuidNotAndUserNameStartingWith(anyString(), anyString());
    }

    @Test
    @DisplayName("Should get user profile successfully")
    void testGetProfile_Success() throws Exception {
        // Arrange
        setupAuthentication();
        
        when(userRepository.findByUserName("testuser"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.findByUuid("user-uuid-1"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.findByUuid("user-uuid-2"))
                .thenReturn(Optional.of(otherUser));
        when(followRepository.countByFollowerId(otherUser.getId())).thenReturn(5L);
        when(followRepository.countByFollowingId(otherUser.getId())).thenReturn(10L);
        when(followRepository.existsByFollowerIdAndFollowingId(testUser.getId(), otherUser.getId()))
                .thenReturn(true);

        // Act
        Optional<UsersRespons> result = usersServices.getProfile("user-uuid-2");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("otheruser");
        assertThat(result.get().getConnect()).isTrue();

        verify(userRepository).findByUuid("user-uuid-2");
    }

    @Test
    @DisplayName("Should get my followers successfully")
    void testGetMyFollowers_Success() throws Exception {
        // Arrange
        setupAuthentication();
        
        List<User> followers = Collections.singletonList(otherUser);
        when(userRepository.findByUserName("testuser"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.findByUuid("user-uuid-1"))
                .thenReturn(Optional.of(testUser));
        when(followRepository.findFollowersByUserId(testUser.getId()))
                .thenReturn(followers);

        // Act
        List<FollowUserResponse> result = usersServices.getMyFollowers();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("otheruser");

        verify(followRepository).findFollowersByUserId(testUser.getId());
    }

    @Test
    @DisplayName("Should get my following successfully")
    void testGetMyFollowing_Success() throws Exception {
        // Arrange
        setupAuthentication();
        
        List<User> following = Collections.singletonList(otherUser);
        when(userRepository.findByUserName("testuser"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.findByUuid("user-uuid-1"))
                .thenReturn(Optional.of(testUser));
        when(followRepository.findFollowingByUserId(testUser.getId()))
                .thenReturn(following);

        // Act
        List<FollowUserResponse> result = usersServices.getMyFollowing();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("otheruser");

        verify(followRepository).findFollowingByUserId(testUser.getId());
    }

    private void setupAuthentication() throws Exception {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUserName("testuser"))
                .thenReturn(Optional.of(testUser));
        when(followRepository.countByFollowerId(testUser.getId())).thenReturn(5L);
        when(followRepository.countByFollowingId(testUser.getId())).thenReturn(10L);
    }
}



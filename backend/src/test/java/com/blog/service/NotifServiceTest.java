package com.blog.service;

import com.blog.dto.NotificationRequest;
import com.blog.dto.NotificationResponse;
import com.blog.dto.UsersRespons;
import com.blog.entity.Notification;
import com.blog.entity.Post;
import com.blog.exceptions.UserNotLoginException;
import com.blog.repository.NotifRepository;
import com.blog.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotifService Tests")
class NotifServiceTest {

    @Mock
    private NotifRepository notifRepository;

    @Mock
    private UsersServices usersServices;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private NotifService notifService;

    private UsersRespons testUserRespons;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testUserRespons = new UsersRespons();
        testUserRespons.setUuid("user-uuid-123");
        testUserRespons.setUsername("testuser");

        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setUuid("notif-uuid-123");
        testNotification.setNotification("Test notification");
        testNotification.setNotificatedUser("user-uuid-123");
        testNotification.setNotificationOwner("user-uuid-456");
        testNotification.setPostOrProfileUuid("post-uuid-123");
        testNotification.setCreatedAt(System.currentTimeMillis());
        testNotification.setIsRead(false);
    }

    @Test
    @DisplayName("Should get notifications successfully")
    void testGetNotifications_Success() throws Exception {
        // Arrange
        List<Notification> notifications = new ArrayList<>();
        notifications.add(testNotification);

        when(usersServices.getCurrentUser()).thenReturn(testUserRespons);
        when(notifRepository.findByNotificatedUserOrderByIdDesc("user-uuid-123"))
                .thenReturn(notifications);

        // Act
        List<NotificationResponse> result = notifService.getNotifications();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUuid()).isEqualTo("notif-uuid-123");
        assertThat(result.get(0).getContent()).isEqualTo("Test notification");
        assertThat(result.get(0).getIsRead()).isFalse();

        verify(notifRepository).findByNotificatedUserOrderByIdDesc("user-uuid-123");
    }

    @Test
    @DisplayName("Should filter out notifications for hidden posts")
    void testGetNotifications_FilterHiddenPosts() throws Exception {
        // Arrange
        Post hiddenPost = new Post();
        hiddenPost.setUuid("post-uuid-123");
        hiddenPost.setStatus("HIDE");

        List<Notification> notifications = new ArrayList<>();
        notifications.add(testNotification);

        when(usersServices.getCurrentUser()).thenReturn(testUserRespons);
        when(notifRepository.findByNotificatedUserOrderByIdDesc("user-uuid-123"))
                .thenReturn(notifications);
        when(postRepository.findByUuid("post-uuid-123"))
                .thenReturn(Optional.of(hiddenPost));

        // Act
        List<NotificationResponse> result = notifService.getNotifications();

        // Assert
        assertThat(result).isEmpty();
        verify(postRepository).findByUuid("post-uuid-123");
    }

    @Test
    @DisplayName("Should throw UserNotLoginException when user not authenticated")
    void testGetNotifications_UserNotAuthenticated() throws Exception {
        // Arrange
        when(usersServices.getCurrentUser())
                .thenThrow(new RuntimeException("Not authenticated"));

        // Act & Assert
        assertThatThrownBy(() -> notifService.getNotifications())
                .isInstanceOf(UserNotLoginException.class)
                .hasMessage("loggin or register if not so u can get notif");
    }

    @Test
    @DisplayName("Should mark notification as read")
    void testReadingNotif_Success() {
        // Arrange
        NotificationRequest request = new NotificationRequest();
        request.setUuid("notif-uuid-123");

        // Act
        notifService.readingNotif(request);

        // Assert
        verify(notifRepository).markAsRead("notif-uuid-123");
    }

    @Test
    @DisplayName("Should count unread notifications successfully")
    void testCountUnreadNotif_Success() throws Exception {
        // Arrange
        when(usersServices.getCurrentUser()).thenReturn(testUserRespons);
        when(notifRepository.countUnreadNotificationsExcludingHiddenPosts("user-uuid-123"))
                .thenReturn(5L);

        // Act
        long result = notifService.countUnreadNotif();

        // Assert
        assertThat(result).isEqualTo(5L);
        verify(notifRepository).countUnreadNotificationsExcludingHiddenPosts("user-uuid-123");
    }

    @Test
    @DisplayName("Should throw UserNotLoginException when counting unread notifications and user not logged in")
    void testCountUnreadNotif_UserNotAuthenticated() throws Exception {
        // Arrange
        when(usersServices.getCurrentUser())
                .thenThrow(new RuntimeException("Not authenticated"));

        // Act & Assert
        assertThatThrownBy(() -> notifService.countUnreadNotif())
                .isInstanceOf(UserNotLoginException.class)
                .hasMessage("login for getting notification count");
    }

    @Test
    @DisplayName("Should get paginated notifications successfully")
    void testGetNotificationsPaginated_Success() throws Exception {
        // Arrange
        List<Notification> notifications = new ArrayList<>();
        notifications.add(testNotification);

        when(usersServices.getCurrentUser()).thenReturn(testUserRespons);
        when(notifRepository.findByNotificatedUserPaginated(
                eq("user-uuid-123"), anyLong(), isNull(), any(Pageable.class)))
                .thenReturn(notifications);

        // Act
        NotifService.NotificationPage result = notifService.getNotificationsPaginated(null, null);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.notifications()).hasSize(1);
        assertThat(result.notifications().get(0).getUuid()).isEqualTo("notif-uuid-123");
        assertThat(result.lastUuid()).isEqualTo("1");

        verify(notifRepository).findByNotificatedUserPaginated(
                eq("user-uuid-123"), anyLong(), isNull(), any(Pageable.class));
    }

    @Test
    @DisplayName("Should get paginated notifications with lastTime and lastUuid")
    void testGetNotificationsPaginated_WithParams() throws Exception {
        // Arrange
        Long lastTime = 1234567890L;
        String lastUuid = "1";

        Notification lastNotification = new Notification();
        lastNotification.setId(1L);
        lastNotification.setCreatedAt(lastTime);

        List<Notification> notifications = new ArrayList<>();
        notifications.add(testNotification);

        when(usersServices.getCurrentUser()).thenReturn(testUserRespons);
        when(notifRepository.findById(1L)).thenReturn(Optional.of(lastNotification));
        when(notifRepository.findByNotificatedUserPaginated(
                eq("user-uuid-123"), eq(lastTime), eq(1L), any(Pageable.class)))
                .thenReturn(notifications);

        // Act
        NotifService.NotificationPage result = notifService.getNotificationsPaginated(lastTime, lastUuid);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.notifications()).hasSize(1);
        verify(notifRepository).findById(1L);
        verify(notifRepository).findByNotificatedUserPaginated(
                eq("user-uuid-123"), eq(lastTime), eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("Should return empty notification page when no notifications")
    void testGetNotificationsPaginated_Empty() throws Exception {
        // Arrange
        when(usersServices.getCurrentUser()).thenReturn(testUserRespons);
        when(notifRepository.findByNotificatedUserPaginated(
                eq("user-uuid-123"), anyLong(), isNull(), any(Pageable.class)))
                .thenReturn(new ArrayList<>());

        // Act
        NotifService.NotificationPage result = notifService.getNotificationsPaginated(null, null);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.notifications()).isEmpty();
        assertThat(result.lastUuid()).isNull();
        assertThat(result.lastTime()).isNull();
    }

    @Test
    @DisplayName("Should throw UserNotLoginException when getting paginated notifications and user not logged in")
    void testGetNotificationsPaginated_UserNotAuthenticated() throws Exception {
        // Arrange
        when(usersServices.getCurrentUser())
                .thenThrow(new RuntimeException("Not authenticated"));

        // Act & Assert
        assertThatThrownBy(() -> notifService.getNotificationsPaginated(null, null))
                .isInstanceOf(UserNotLoginException.class)
                .hasMessage("login or register to get notifications");
    }
}



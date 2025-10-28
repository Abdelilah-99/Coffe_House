package com.blog.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import com.blog.entity.Notification;
import com.blog.entity.Post;
import com.blog.dto.NotificationResponse;
import com.blog.dto.NotificationRequest;
import com.blog.dto.UsersRespons;
import com.blog.repository.NotifRepository;
import com.blog.repository.PostRepository;
import com.blog.exceptions.UserNotLoginException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class NotifService {
    private final NotifRepository notifRepository;
    private final UsersServices usersServices;
    private final PostRepository postRepository;

    NotifService(
            NotifRepository notifRepository,
            UsersServices usersServices,
            PostRepository postRepository) {
        this.notifRepository = notifRepository;
        this.usersServices = usersServices;
        this.postRepository = postRepository;
    }

    public List<NotificationResponse> getNotifications() {
        UsersRespons usersRespons;
        try {
            usersRespons = usersServices.getCurrentUser();
        } catch (Exception e) {
            throw new UserNotLoginException("loggin or register if not so u can get notif");
        }
        List<Notification> notifications = notifRepository.findByNotificatedUserOrderByIdDesc(usersRespons.getUuid());
        List<NotificationResponse> notifDtos = toNotifDto(notifications);
        return notifDtos;
    }

    private List<NotificationResponse> toNotifDto(List<Notification> notifications) {
        List<NotificationResponse> notificationResponseList = new ArrayList<>();
        for (Notification notification : notifications) {
            String postOrProfileUuid = notification.getPostOrProfileUuid();
            if (postOrProfileUuid != null) {
                Post post = postRepository.findByUuid(postOrProfileUuid).orElse(null);
                if (post != null && "HIDE".equals(post.getStatus())) {
                    continue;
                }
            }
            NotificationResponse notificationResponse = new NotificationResponse();
            notificationResponse.setContent(notification.getNotification());
            notificationResponse.setUuid(notification.getUuid());
            notificationResponse.setPostOrProfileUuid(notification.getPostOrProfileUuid());
            notificationResponse.setTime(notification.getCreatedAt());
            notificationResponse.setIsRead(notification.getIsRead());
            notificationResponseList.add(notificationResponse);
        }
        return notificationResponseList;
    }

    public Void readingNotif(NotificationRequest req) {
        notifRepository.markAsRead(req.getUuid());
        return null;
    }

    public long countUnreadNotif() {
        UsersRespons usersRespons;
        try {
            usersRespons = usersServices.getCurrentUser();
        } catch (Exception e) {
            throw new UserNotLoginException("login for getting notification count");
        }
        return notifRepository.countUnreadNotificationsExcludingHiddenPosts(usersRespons.getUuid());
    }

    public record NotificationPage(List<NotificationResponse> notifications, Long lastTime, String lastUuid) {
    }

    public NotificationPage getNotificationsPaginated(Long lastTime, String lastUuid) {
        UsersRespons usersRespons;
        try {
            usersRespons = usersServices.getCurrentUser();
        } catch (Exception e) {
            throw new UserNotLoginException("login or register to get notifications");
        }

        Pageable pageable = PageRequest.of(0, 10);

        if (lastTime == null) {
            lastTime = System.currentTimeMillis() + 1000;
        }

        Long lastId = null;
        if (lastUuid != null) {
            Notification notification = notifRepository.findById(Long.parseLong(lastUuid)).orElse(null);
            if (notification != null) {
                lastId = notification.getId();
            }
        }

        List<Notification> notifications = notifRepository.findByNotificatedUserPaginated(
            usersRespons.getUuid(), lastTime, lastId, pageable);

        List<NotificationResponse> notifDtos = toNotifDto(notifications);

        String newLastUuid = notifications.isEmpty() ? null : String.valueOf(notifications.get(notifications.size() - 1).getId());
        Long newLastTime = notifications.isEmpty() ? null : notifications.get(notifications.size() - 1).getCreatedAt();

        return new NotificationPage(notifDtos, newLastTime, newLastUuid);
    }
}

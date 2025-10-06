package com.blog.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import com.blog.entity.Notification;
import com.blog.dto.NotificationResponse;
import com.blog.dto.NotificationRequest;
import com.blog.dto.UsersRespons;
import com.blog.repository.NotifRepository;
import com.blog.exceptions.UserNotLoginException;

@Service
public class NotifService {
    private final NotifRepository notifRepository;
    private final UsersServices usersServices;

    NotifService(
            NotifRepository notifRepository,
            UsersServices usersServices) {
        this.notifRepository = notifRepository;
        this.usersServices = usersServices;
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
            NotificationResponse notificationResponse = new NotificationResponse();
            notificationResponse.setContent(notification.getNotification());
            notificationResponse.setUuid(notification.getUuid());
            notificationResponse.setPostOrProfileUuid(notification.getPostOrProfileUuid());
            notificationResponse.setTime(notification.getCreatedAt());
            notificationResponse.setRead(notification.getIsRead());
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
        System.out.println("login user for notif: " + usersRespons.getUsername());
        return notifRepository.countByNotificatedUserAndIsReadFalse(usersRespons.getUuid());
    }
}

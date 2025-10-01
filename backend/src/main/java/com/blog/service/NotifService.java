package com.blog.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import com.blog.entity.Notification;
import com.blog.dto.NotificationResponse;
import com.blog.dto.NotificationRequest;
import com.blog.dto.UsersRespons;
import com.blog.repository.NotifRepository;

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

    public List<NotificationResponse> getNotifications(NotificationRequest req) {
        List<Notification> notifications = notifRepository.findByUserUuid(req.getUuid());
        List<NotificationResponse> notifDtos = toNotifDto(notifications);
        return notifDtos;
    }

    private List<NotificationResponse> toNotifDto(List<Notification> notifications) {
        List<NotificationResponse> notificationResponseList = new ArrayList<>();
        for (Notification notification : notifications) {
            NotificationResponse notificationResponse = new NotificationResponse();
            notificationResponse.setContent(notification.getNotification());
            notificationResponse.setUuid(notification.getUuid());
            notificationResponse.setTime(notification.getCreatedAt());
            notificationResponseList.add(notificationResponse);
        }
        return notificationResponseList;
    }
}

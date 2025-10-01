package com.blog.controller;

import com.blog.dto.NotificationResponse;
import com.blog.service.NotifService;
import com.blog.dto.NotificationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notif")
public class NotificationsController {
    private NotifService notifService;

    NotificationsController(NotifService notifService) {
        this.notifService = notifService;
    }

    @PostMapping("/all")
    public ResponseEntity<List<NotificationResponse>> getAllNotif(@RequestBody NotificationRequest req) {
        List<NotificationResponse> res = notifService.getNotifications(req);
        return ResponseEntity.ok(res);
    }

    // @PostMapping("/read")
    // pubic ResponseEntity<NotificationResponse> markRead(@RequestBody )
}

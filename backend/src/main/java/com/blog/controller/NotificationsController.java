package com.blog.controller;

import com.blog.dto.NotificationResponse;
import com.blog.service.NotifService;
import com.blog.service.NotifService.NotificationPage;
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

    @GetMapping("/all")
    public ResponseEntity<List<NotificationResponse>> getAllNotif() {
        List<NotificationResponse> res = notifService.getNotifications();
        return ResponseEntity.ok(res);
    }

    @PostMapping("/read")
    public ResponseEntity<Void> markRead(@RequestBody NotificationRequest req) {
        return ResponseEntity.ok(notifService.readingNotif(req));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> notifCount() {
        return ResponseEntity.ok(notifService.countUnreadNotif());
    }

    @GetMapping("/pages")
    public ResponseEntity<NotificationPage> getNotificationsByPage(
            @RequestParam(value = "lastTime", required = false) Long lastTime,
            @RequestParam(value = "lastUuid", required = false) String lastUuid) {
        NotificationPage data = notifService.getNotificationsPaginated(lastTime, lastUuid);
        return ResponseEntity.ok(data);
    }
}

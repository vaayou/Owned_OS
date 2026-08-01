package com.owned.user_service.controller;

import com.owned.user_service.dto.NotificationResponse;
import com.owned.user_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Notification feed endpoint.
 */
@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * GET /users/me/notifications
     * Returns notifications newest-first and marks all unread entries as read.
     */
    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationResponse>> getNotifications(Authentication auth) {
        Long userId = extractUserId(auth);
        return ResponseEntity.ok(notificationService.getAndMarkRead(userId));
    }

    // ── helpers ─────────────────────────────────────────────────────────────────

    private Long extractUserId(Authentication auth) {
        if (auth == null || auth.getDetails() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return (Long) auth.getDetails();
    }
}

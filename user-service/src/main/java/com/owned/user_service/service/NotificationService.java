package com.owned.user_service.service;

import com.owned.user_service.dto.NotificationResponse;
import com.owned.user_service.entity.Notification;
import com.owned.user_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Manages notification retrieval and persistence.
 *
 * <p>Notifications are created by {@link com.owned.user_service.kafka.UserEventConsumer}
 * when events arrive from other services via Kafka.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /** Returns all notifications for a user and marks unread ones as read. */
    @Transactional
    public List<NotificationResponse> getAndMarkRead(Long userId) {
        List<NotificationResponse> result = notificationRepository
                .findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResponse::from)
                .toList();

        notificationRepository.markAllReadByUserId(userId);
        return result;
    }

    /** Persists a new notification. Called from the Kafka consumer. */
    @Transactional
    public void createNotification(Long userId, Notification.Type type, String payloadJson) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .payloadJson(payloadJson)
                .build();
        notificationRepository.save(notification);
    }
}

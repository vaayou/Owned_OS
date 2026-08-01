package com.owned.user_service.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.owned.user_service.entity.Notification;
import com.owned.user_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumes events from the {@code user-events} Kafka topic and persists
 * them as in-app notifications.
 *
 * <p>Expected message payload (JSON):
 * <pre>{@code
 * {
 *   "userId": 42,
 *   "type":   "PURCHASE" | "FOLLOW" | "SYSTEM",
 *   "payload": { ... }   // arbitrary event data stored verbatim
 * }
 * }</pre>
 *
 * <p>Malformed or unrecognised messages are logged and skipped — the
 * consumer does NOT re-throw, preventing infinite retry loops on bad
 * data while still allowing the offset to advance.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private static final String TOPIC = "user-events";

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        log.debug("Received Kafka message on [{}]: {}", TOPIC, message);
        try {
            JsonNode root = objectMapper.readTree(message);

            Long userId = root.path("userId").asLong(0);
            String typeStr = root.path("type").asText(null);
            JsonNode payloadNode = root.path("payload");

            if (userId == 0 || typeStr == null) {
                log.warn("Skipping malformed user-event message: missing userId or type");
                return;
            }

            Notification.Type type;
            try {
                type = Notification.Type.valueOf(typeStr.toUpperCase());
            } catch (IllegalArgumentException ex) {
                log.warn("Unknown notification type '{}', skipping", typeStr);
                return;
            }

            String payloadJson = payloadNode.isMissingNode()
                    ? null
                    : objectMapper.writeValueAsString(payloadNode);

            notificationService.createNotification(userId, type, payloadJson);
            log.info("Notification created for userId={} type={}", userId, type);

        } catch (Exception ex) {
            log.error("Failed to process user-event message: {}", message, ex);
        }
    }
}

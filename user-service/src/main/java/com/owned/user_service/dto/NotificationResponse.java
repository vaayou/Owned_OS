package com.owned.user_service.dto;

import com.owned.user_service.entity.Notification;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class NotificationResponse {

    private Long id;
    private Notification.Type type;
    private String payloadJson;
    private boolean read;
    private Instant createdAt;

    public static NotificationResponse from(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .payloadJson(n.getPayloadJson())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}

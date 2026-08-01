package com.owned.user_service.dto;

import com.owned.user_service.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UserProfileResponse {

    private Long id;
    private String username;
    private String avatarUrl;
    private boolean isPublic;
    private Instant createdAt;

    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .isPublic(user.isPublic())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

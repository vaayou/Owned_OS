package com.owned.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;     // seconds
    private Long userId;
    private String username;

    public static AuthResponse of(String accessToken, String refreshToken,
                                  long expiresInSeconds, Long userId, String username) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresInSeconds)
                .userId(userId)
                .username(username)
                .build();
    }
}

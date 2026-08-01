package com.owned.user_service.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {

    private String avatarUrl;

    private Boolean isPublic;
}

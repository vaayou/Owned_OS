package com.owned.commerce_service.security;

import com.owned.commerce_service.exception.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof Long userId) {
            return userId;
        }
        throw new BadRequestException("User is not authenticated or missing user ID header");
    }
}

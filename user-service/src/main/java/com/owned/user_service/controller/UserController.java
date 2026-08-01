package com.owned.user_service.controller;

import com.owned.user_service.dto.UpdateProfileRequest;
import com.owned.user_service.dto.UserProfileResponse;
import com.owned.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * User profile endpoints.
 *
 * <p>All requests arriving here have already been JWT-validated by the Gateway.
 * The authenticated user's id is stored in {@link Authentication#getDetails()}.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** GET /users/{id} — return public profile (respects is_public gate) */
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getProfile(
            @PathVariable Long id,
            Authentication auth
    ) {
        Long requesterId = extractUserIdNullable(auth);
        return ResponseEntity.ok(userService.getPublicProfile(id, requesterId));
    }

    /** PUT /users/me — update the authenticated user's profile */
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestBody UpdateProfileRequest req,
            Authentication auth
    ) {
        Long userId = extractUserId(auth);
        return ResponseEntity.ok(userService.updateProfile(userId, req));
    }

    // ── helpers ─────────────────────────────────────────────────────────────────

    private Long extractUserId(Authentication auth) {
        if (auth == null || auth.getDetails() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED);
        }
        return (Long) auth.getDetails();
    }

    private Long extractUserIdNullable(Authentication auth) {
        if (auth == null || auth.getDetails() == null) {
            return null;
        }
        return (Long) auth.getDetails();
    }
}

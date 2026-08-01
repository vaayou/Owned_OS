package com.owned.user_service.controller;

import com.owned.user_service.dto.UserProfileResponse;
import com.owned.user_service.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Social graph endpoints — follow, unfollow, list followers.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    /** POST /users/{id}/follow — follow the specified user */
    @PostMapping("/{id}/follow")
    public ResponseEntity<Void> follow(
            @PathVariable Long id,
            Authentication auth
    ) {
        Long followerId = extractUserId(auth);
        followService.follow(followerId, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /** DELETE /users/{id}/follow — unfollow the specified user */
    @DeleteMapping("/{id}/follow")
    public ResponseEntity<Void> unfollow(
            @PathVariable Long id,
            Authentication auth
    ) {
        Long followerId = extractUserId(auth);
        followService.unfollow(followerId, id);
        return ResponseEntity.noContent().build();
    }

    /** GET /users/{id}/followers — list the followers of the specified user */
    @GetMapping("/{id}/followers")
    public ResponseEntity<List<UserProfileResponse>> getFollowers(@PathVariable Long id) {
        return ResponseEntity.ok(followService.getFollowers(id));
    }

    // ── helpers ─────────────────────────────────────────────────────────────────

    private Long extractUserId(Authentication auth) {
        if (auth == null || auth.getDetails() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return (Long) auth.getDetails();
    }
}

package com.owned.collection_service.controller;

import com.owned.collection_service.dto.CollectionItemResponse;
import com.owned.collection_service.dto.LeaderboardEntryDto;
import com.owned.collection_service.dto.NetWorthResponse;
import com.owned.collection_service.service.CollectionService;
import com.owned.collection_service.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/collection")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;
    private final LeaderboardService leaderboardService;

    /**
     * GET /collection/me
     * Returns authenticated user's owned items
     */
    @GetMapping("/me")
    public ResponseEntity<List<CollectionItemResponse>> getMyCollection(Authentication auth) {
        Long userId = extractUserId(auth);
        return ResponseEntity.ok(collectionService.getUserCollection(userId));
    }

    /**
     * GET /collection/{userId}
     * Returns public collection for a specific user ID
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<CollectionItemResponse>> getPublicCollection(@PathVariable Long userId) {
        return ResponseEntity.ok(collectionService.getUserCollection(userId));
    }

    /**
     * GET /collection/me/networth
     * Returns total net worth and total item count for authenticated user
     */
    @GetMapping("/me/networth")
    public ResponseEntity<NetWorthResponse> getMyNetWorth(Authentication auth) {
        Long userId = extractUserId(auth);
        return ResponseEntity.ok(leaderboardService.getUserNetWorth(userId));
    }

    /**
     * GET /collection/leaderboard
     * Returns cached top collectors leaderboard
     */
    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardEntryDto>> getLeaderboard() {
        return ResponseEntity.ok(leaderboardService.getLeaderboard());
    }

    // ── Helper method ─────────────────────────────────────────────────────────────

    private Long extractUserId(Authentication auth) {
        if (auth == null || auth.getDetails() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User authentication missing or invalid");
        }
        return (Long) auth.getDetails();
    }
}

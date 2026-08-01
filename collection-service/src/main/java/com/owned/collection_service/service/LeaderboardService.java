package com.owned.collection_service.service;

import com.owned.collection_service.dto.LeaderboardEntryDto;
import com.owned.collection_service.dto.LeaderboardProjection;
import com.owned.collection_service.dto.NetWorthResponse;
import com.owned.collection_service.repository.CollectionItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final CollectionItemRepository collectionItemRepository;

    @Transactional(readOnly = true)
    public NetWorthResponse getUserNetWorth(Long userId) {
        BigDecimal totalNetWorth = collectionItemRepository.sumPriceCoinsByUserId(userId);
        if (totalNetWorth == null) {
            totalNetWorth = BigDecimal.ZERO;
        }
        long itemCount = collectionItemRepository.countByUserId(userId);

        return NetWorthResponse.builder()
                .userId(userId)
                .totalNetWorth(totalNetWorth)
                .totalItemsOwned(itemCount)
                .build();
    }

    @Cacheable(value = "leaderboard")
    @Transactional(readOnly = true)
    public List<LeaderboardEntryDto> getLeaderboard() {
        log.info("Computing top collectors leaderboard from database snapshots...");
        List<LeaderboardProjection> projections = collectionItemRepository.findLeaderboardProjections();

        List<LeaderboardEntryDto> result = new ArrayList<>();
        int rank = 1;

        for (LeaderboardProjection proj : projections) {
            result.add(LeaderboardEntryDto.builder()
                    .rank(rank++)
                    .userId(proj.getUserId())
                    .totalNetWorth(proj.getTotalNetWorth() != null ? proj.getTotalNetWorth() : BigDecimal.ZERO)
                    .totalItemsOwned(proj.getTotalItemsOwned() != null ? proj.getTotalItemsOwned() : 0)
                    .build());
        }

        return result;
    }

    @CacheEvict(value = "leaderboard", allEntries = true)
    public void evictLeaderboardCache() {
        log.info("Leaderboard cache evicted after new acquisition.");
    }
}

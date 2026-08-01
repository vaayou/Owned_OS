package com.owned.collection_service.repository;

import com.owned.collection_service.dto.LeaderboardProjection;
import com.owned.collection_service.entity.CollectionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CollectionItemRepository extends JpaRepository<CollectionItem, Long> {

    List<CollectionItem> findByUserIdOrderByAcquiredAtDesc(Long userId);

    @Query("SELECT SUM(c.priceCoinsSnapshot) FROM CollectionItem c WHERE c.userId = :userId")
    BigDecimal sumPriceCoinsByUserId(Long userId);

    long countByUserId(Long userId);

    @Query("SELECT new com.owned.collection_service.dto.LeaderboardProjection(c.userId, SUM(c.priceCoinsSnapshot), COUNT(c.id)) " +
           "FROM CollectionItem c " +
           "GROUP BY c.userId " +
           "ORDER BY SUM(c.priceCoinsSnapshot) DESC")
    List<LeaderboardProjection> findLeaderboardProjections();
}

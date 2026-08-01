package com.owned.collection_service.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardProjection {
    private Long userId;
    private BigDecimal totalNetWorth;
    private Long totalItemsOwned;
}

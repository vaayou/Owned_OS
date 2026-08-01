package com.owned.collection_service.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardEntryDto {
    private int rank;
    private Long userId;
    private BigDecimal totalNetWorth;
    private long totalItemsOwned;
}

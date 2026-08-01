package com.owned.commerce_service.dto;

import com.owned.commerce_service.entity.Wallet;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletResponse {

    private Long id;
    private Long userId;
    private BigDecimal coinBalance;
    private Instant updatedAt;
    private List<WalletTransactionResponse> recentTransactions;

    public static WalletResponse fromEntity(Wallet wallet, List<WalletTransactionResponse> transactions) {
        if (wallet == null) return null;
        return WalletResponse.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .coinBalance(wallet.getCoinBalance())
                .updatedAt(wallet.getUpdatedAt())
                .recentTransactions(transactions != null ? transactions : List.of())
                .build();
    }
}

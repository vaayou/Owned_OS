package com.owned.commerce_service.dto;

import com.owned.commerce_service.entity.TransactionType;
import com.owned.commerce_service.entity.WalletTransaction;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletTransactionResponse {

    private Long id;
    private TransactionType type;
    private BigDecimal coinAmount;
    private BigDecimal realMoneyAmount;
    private Long paymentRecordId;
    private Instant createdAt;

    public static WalletTransactionResponse fromEntity(WalletTransaction transaction) {
        if (transaction == null) return null;
        return WalletTransactionResponse.builder()
                .id(transaction.getId())
                .type(transaction.getType())
                .coinAmount(transaction.getCoinAmount())
                .realMoneyAmount(transaction.getRealMoneyAmount())
                .paymentRecordId(transaction.getPaymentRecord() != null ? transaction.getPaymentRecord().getId() : null)
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}

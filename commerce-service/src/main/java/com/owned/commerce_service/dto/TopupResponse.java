package com.owned.commerce_service.dto;

import com.owned.commerce_service.entity.PaymentProvider;
import com.owned.commerce_service.entity.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopupResponse {

    private Long paymentRecordId;
    private PaymentProvider provider;
    private String providerTxnId;
    private BigDecimal amountInr;
    private BigDecimal expectedCoins;
    private PaymentStatus status;
    private String checkoutUrl;
}

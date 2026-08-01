package com.owned.commerce_service.dto;

import com.owned.commerce_service.entity.PaymentProvider;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookPayload {

    private PaymentProvider provider;
    private String providerTxnId;
    private String eventType;
    private BigDecimal amountInr;
    private String signature;
    private String rawBody;
}

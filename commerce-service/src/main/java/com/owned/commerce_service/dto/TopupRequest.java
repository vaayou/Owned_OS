package com.owned.commerce_service.dto;

import com.owned.commerce_service.entity.PaymentProvider;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopupRequest {

    @NotNull(message = "Amount in INR is required")
    @DecimalMin(value = "1.00", message = "Amount must be at least ₹1")
    private BigDecimal amountInr;

    @NotNull(message = "Payment provider is required")
    private PaymentProvider provider;
}

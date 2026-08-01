package com.owned.commerce_service.event;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCompletedEvent {

    private Long orderId;
    private Long userId;
    private BigDecimal totalCoins;
    private List<OrderItemDetail> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemDetail {
        private Long productId;
        private String productName;
        private String productImage;
        private BigDecimal priceAtPurchase;
    }
}

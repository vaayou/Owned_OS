package com.owned.commerce_service.dto;

import com.owned.commerce_service.entity.Order;
import com.owned.commerce_service.entity.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private Long userId;
    private OrderStatus status;
    private BigDecimal totalCoins;
    private Instant createdAt;
    private List<OrderItemResponse> items;

    public static OrderResponse fromEntity(Order order) {
        if (order == null) return null;
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .totalCoins(order.getTotalCoins())
                .createdAt(order.getCreatedAt())
                .items(order.getItems() != null
                        ? order.getItems().stream().map(OrderItemResponse::fromEntity).toList()
                        : List.of())
                .build();
    }
}

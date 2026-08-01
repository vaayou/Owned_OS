package com.owned.collection_service.dto;

import com.owned.collection_service.entity.CollectionItem;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionItemResponse {

    private Long id;
    private Long userId;
    private Long productId;
    private String productNameSnapshot;
    private String productImageSnapshot;
    private BigDecimal priceCoinsSnapshot;
    private Long orderId;
    private Instant acquiredAt;

    private Long certificateId;
    private String serialNo;
    private String certificateImageUrl;

    public static CollectionItemResponse fromEntity(CollectionItem item, Long certificateId, String serialNo, String certificateImageUrl) {
        return CollectionItemResponse.builder()
                .id(item.getId())
                .userId(item.getUserId())
                .productId(item.getProductId())
                .productNameSnapshot(item.getProductNameSnapshot())
                .productImageSnapshot(item.getProductImageSnapshot())
                .priceCoinsSnapshot(item.getPriceCoinsSnapshot())
                .orderId(item.getOrderId())
                .acquiredAt(item.getAcquiredAt())
                .certificateId(certificateId)
                .serialNo(serialNo)
                .certificateImageUrl(certificateImageUrl)
                .build();
    }
}

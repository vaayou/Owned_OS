package com.owned.commerce_service.dto;

import com.owned.commerce_service.entity.Product;
import com.owned.commerce_service.entity.Tier;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private Tier categoryTier;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal priceCoins;
    private Boolean isActive;

    public static ProductResponse fromEntity(Product product) {
        if (product == null) return null;
        return ProductResponse.builder()
                .id(product.getId())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .categoryTier(product.getCategory() != null ? product.getCategory().getTier() : null)
                .name(product.getName())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .priceCoins(product.getPriceCoins())
                .isActive(product.getIsActive())
                .build();
    }
}

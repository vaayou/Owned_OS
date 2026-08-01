package com.owned.collection_service.dto;

import com.owned.collection_service.entity.Certificate;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateResponse {
    private Long id;
    private Long collectionItemId;
    private String serialNo;
    private String imageUrl;
    private Instant generatedAt;

    private Long ownerUserId;
    private Long productId;
    private String productName;
    private BigDecimal priceCoins;
    private Instant acquiredAt;

    public static CertificateResponse fromEntity(Certificate cert) {
        return CertificateResponse.builder()
                .id(cert.getId())
                .collectionItemId(cert.getCollectionItem().getId())
                .serialNo(cert.getSerialNo())
                .imageUrl(cert.getImageUrl())
                .generatedAt(cert.getGeneratedAt())
                .ownerUserId(cert.getCollectionItem().getUserId())
                .productId(cert.getCollectionItem().getProductId())
                .productName(cert.getCollectionItem().getProductNameSnapshot())
                .priceCoins(cert.getCollectionItem().getPriceCoinsSnapshot())
                .acquiredAt(cert.getCollectionItem().getAcquiredAt())
                .build();
    }
}

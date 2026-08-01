package com.owned.collection_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.owned.collection_service.dto.CollectionItemResponse;
import com.owned.collection_service.dto.OrderCompletedEvent;
import com.owned.collection_service.entity.Certificate;
import com.owned.collection_service.entity.CollectionItem;
import com.owned.collection_service.repository.CertificateRepository;
import com.owned.collection_service.repository.CollectionItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectionService {

    private static final String USER_EVENTS_TOPIC = "user-events";

    private final CollectionItemRepository collectionItemRepository;
    private final CertificateRepository certificateRepository;
    private final CertificateService certificateService;
    private final LeaderboardService leaderboardService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public void addItemsFromOrder(OrderCompletedEvent event) {
        log.info("Processing OrderCompleted event for orderId: {}, userId: {}, items count: {}",
                event.getOrderId(), event.getUserId(), event.getItems() != null ? event.getItems().size() : 0);

        if (event.getItems() == null || event.getItems().isEmpty()) {
            log.warn("No items found in OrderCompletedEvent for orderId: {}", event.getOrderId());
            return;
        }

        for (OrderCompletedEvent.OrderItemDetail itemDetail : event.getItems()) {
            CollectionItem collectionItem = CollectionItem.builder()
                    .userId(event.getUserId())
                    .productId(itemDetail.getProductId())
                    .productNameSnapshot(itemDetail.getProductName())
                    .productImageSnapshot(itemDetail.getProductImage())
                    .priceCoinsSnapshot(itemDetail.getPriceAtPurchase())
                    .orderId(event.getOrderId())
                    .build();

            CollectionItem savedItem = collectionItemRepository.save(collectionItem);
            log.info("Added collection item ID: {} for user: {}", savedItem.getId(), savedItem.getUserId());

            // Generate Certificate asynchronously / inline
            try {
                Certificate cert = certificateService.generateCertificate(savedItem);
                log.info("Certificate {} created for collection item {}", cert.getSerialNo(), savedItem.getId());
            } catch (Exception e) {
                log.error("Failed to generate certificate for collection item {}: {}", savedItem.getId(), e.getMessage(), e);
            }

            // Publish ItemAcquired event to Kafka for User Service notifications
            publishItemAcquiredEvent(savedItem);
        }

        // Evict leaderboard cache so rankings recalculate on next request
        leaderboardService.evictLeaderboardCache();
    }

    @Transactional(readOnly = true)
    public List<CollectionItemResponse> getUserCollection(Long userId) {
        List<CollectionItem> items = collectionItemRepository.findByUserIdOrderByAcquiredAtDesc(userId);

        return items.stream().map(item -> {
            Optional<Certificate> certOpt = certificateRepository.findByCollectionItemId(item.getId());
            Long certId = certOpt.map(Certificate::getId).orElse(null);
            String serialNo = certOpt.map(Certificate::getSerialNo).orElse(null);
            String imageUrl = certOpt.map(Certificate::getImageUrl).orElse(null);

            return CollectionItemResponse.fromEntity(item, certId, serialNo, imageUrl);
        }).toList();
    }

    private void publishItemAcquiredEvent(CollectionItem item) {
        try {
            Map<String, Object> eventPayload = new HashMap<>();
            eventPayload.put("userId", item.getUserId());
            eventPayload.put("type", "ITEM_ACQUIRED");

            Map<String, Object> details = new HashMap<>();
            details.put("collectionItemId", item.getId());
            details.put("productId", item.getProductId());
            details.put("productName", item.getProductNameSnapshot());
            details.put("orderId", item.getOrderId());
            details.put("priceCoins", item.getPriceCoinsSnapshot());

            eventPayload.put("payload", details);

            String messageJson = objectMapper.writeValueAsString(eventPayload);
            kafkaTemplate.send(USER_EVENTS_TOPIC, item.getUserId().toString(), messageJson);
            log.info("Published 'ItemAcquired' event to topic '{}' for userId: {}", USER_EVENTS_TOPIC, item.getUserId());
        } catch (Exception e) {
            log.error("Failed to publish ItemAcquired event to Kafka for collectionItemId {}: {}",
                    item.getId(), e.getMessage(), e);
        }
    }
}

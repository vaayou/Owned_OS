package com.owned.collection_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.owned.collection_service.dto.OrderCompletedEvent;
import com.owned.collection_service.service.CollectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCompletedConsumer {

    private final CollectionService collectionService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = {"order-completed-topic", "orders"},
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeOrderCompleted(Object record) {
        log.info("Received OrderCompleted message from Kafka: {}", record);
        try {
            OrderCompletedEvent event;
            if (record instanceof String jsonStr) {
                event = objectMapper.readValue(jsonStr, OrderCompletedEvent.class);
            } else if (record instanceof OrderCompletedEvent evt) {
                event = evt;
            } else {
                String jsonStr = objectMapper.writeValueAsString(record);
                event = objectMapper.readValue(jsonStr, OrderCompletedEvent.class);
            }

            if (event != null && event.getOrderId() != null) {
                collectionService.addItemsFromOrder(event);
                log.info("Successfully processed OrderCompleted event for orderId: {}", event.getOrderId());
            } else {
                log.warn("Received empty or invalid OrderCompletedEvent payload: {}", record);
            }
        } catch (Exception e) {
            log.error("Failed to process OrderCompleted event: {}", e.getMessage(), e);
        }
    }
}

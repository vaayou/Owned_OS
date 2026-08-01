package com.owned.commerce_service.service;

import com.owned.commerce_service.dto.CartItemDto;
import com.owned.commerce_service.dto.CheckoutRequest;
import com.owned.commerce_service.dto.OrderResponse;
import com.owned.commerce_service.entity.Order;
import com.owned.commerce_service.entity.OrderItem;
import com.owned.commerce_service.entity.OrderStatus;
import com.owned.commerce_service.entity.Product;
import com.owned.commerce_service.event.OrderCompletedEvent;
import com.owned.commerce_service.exception.BadRequestException;
import com.owned.commerce_service.exception.ResourceNotFoundException;
import com.owned.commerce_service.repository.OrderRepository;
import com.owned.commerce_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private static final String KAFKA_TOPIC = "order-completed-topic";

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final WalletService walletService;
    private final ApplicationEventPublisher eventPublisher;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public OrderResponse checkout(Long userId, CheckoutRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Cart items cannot be empty");
        }

        BigDecimal totalCoins = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        List<OrderCompletedEvent.OrderItemDetail> eventItemDetails = new ArrayList<>();

        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.COMPLETED)
                .build();

        for (CartItemDto cartItem : request.getItems()) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + cartItem.getProductId()));

            if (!Boolean.TRUE.equals(product.getIsActive())) {
                throw new BadRequestException("Product '" + product.getName() + "' is currently inactive");
            }

            int qty = cartItem.getQuantity() != null ? cartItem.getQuantity() : 1;
            BigDecimal lineTotal = product.getPriceCoins().multiply(BigDecimal.valueOf(qty));
            totalCoins = totalCoins.add(lineTotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .priceAtPurchase(product.getPriceCoins())
                    .build();
            orderItems.add(orderItem);

            eventItemDetails.add(OrderCompletedEvent.OrderItemDetail.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .productImage(product.getImageUrl())
                    .priceAtPurchase(product.getPriceCoins())
                    .build());
        }

        order.setTotalCoins(totalCoins);
        order.setItems(orderItems);

        // Debit wallet — throws InsufficientBalanceException if coin_balance < totalCoins
        walletService.debit(userId, totalCoins);

        Order savedOrder = orderRepository.save(order);
        log.info("Checkout successful for user {}. Order ID: {}, Total Coins: {}", userId, savedOrder.getId(), totalCoins);

        // Publish internal Spring application event
        OrderCompletedEvent event = OrderCompletedEvent.builder()
                .orderId(savedOrder.getId())
                .userId(userId)
                .totalCoins(totalCoins)
                .items(eventItemDetails)
                .build();

        eventPublisher.publishEvent(event);

        return OrderResponse.fromEntity(savedOrder);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCompletedAfterCommit(OrderCompletedEvent event) {
        log.info("Transaction committed successfully. Publishing OrderCompleted event for orderId: {} to Kafka topic '{}'",
                event.getOrderId(), KAFKA_TOPIC);
        try {
            kafkaTemplate.send(KAFKA_TOPIC, event.getOrderId().toString(), event);
            log.info("Published OrderCompleted event for orderId: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to publish OrderCompleted event to Kafka for orderId {}: {}",
                    event.getOrderId(), e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersForUser(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(OrderResponse::fromEntity)
                .toList();
    }
}

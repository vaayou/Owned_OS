package com.owned.commerce_service.controller;

import com.owned.commerce_service.dto.CheckoutRequest;
import com.owned.commerce_service.dto.OrderResponse;
import com.owned.commerce_service.security.SecurityUtils;
import com.owned.commerce_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> checkout(@Valid @RequestBody CheckoutRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        OrderResponse response = orderService.checkout(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(orderService.getOrdersForUser(userId));
    }
}

package com.owned.commerce_service.controller;

import com.owned.commerce_service.dto.TopupRequest;
import com.owned.commerce_service.dto.TopupResponse;
import com.owned.commerce_service.dto.WalletResponse;
import com.owned.commerce_service.security.SecurityUtils;
import com.owned.commerce_service.service.PaymentService;
import com.owned.commerce_service.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final PaymentService paymentService;

    @GetMapping("/me")
    public ResponseEntity<WalletResponse> getMyWallet() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(walletService.getWalletResponse(userId));
    }

    @PostMapping("/topup")
    public ResponseEntity<TopupResponse> initiateTopup(@Valid @RequestBody TopupRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(paymentService.initiateTopup(userId, request));
    }
}

package com.owned.commerce_service.controller;

import com.owned.commerce_service.entity.PaymentRecord;
import com.owned.commerce_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/webhook")
    public ResponseEntity<Map<String, Object>> handleWebhook(
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String razorpaySignature,
            @RequestHeader(value = "Stripe-Signature", required = false) String stripeSignature,
            @RequestHeader(value = "X-Webhook-Signature", required = false) String genericSignature,
            @RequestParam(required = false) String providerTxnId,
            @RequestParam(required = false, defaultValue = "payment.succeeded") String eventType,
            @RequestBody(required = false) Map<String, Object> bodyPayload,
            @RequestBody(required = false) String rawPayload
    ) {
        String signature = razorpaySignature != null ? razorpaySignature
                : (stripeSignature != null ? stripeSignature : genericSignature);

        String txnId = providerTxnId;
        if (txnId == null && bodyPayload != null) {
            if (bodyPayload.containsKey("providerTxnId")) {
                txnId = String.valueOf(bodyPayload.get("providerTxnId"));
            } else if (bodyPayload.containsKey("id")) {
                txnId = String.valueOf(bodyPayload.get("id"));
            }
        }

        if (txnId == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "ERROR",
                    "message", "Missing providerTxnId parameter or body field"
            ));
        }

        PaymentRecord record = paymentService.processWebhook(
                txnId,
                eventType,
                signature,
                rawPayload != null ? rawPayload : ""
        );

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "paymentRecordId", record.getId(),
                "providerTxnId", record.getProviderTxnId(),
                "paymentStatus", record.getStatus()
        ));
    }
}

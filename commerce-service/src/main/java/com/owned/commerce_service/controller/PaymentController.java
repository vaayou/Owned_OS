package com.owned.commerce_service.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final PaymentService paymentService;

    @PostMapping("/webhook")
    public ResponseEntity<Map<String, Object>> handleWebhook(
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String razorpaySignature,
            @RequestHeader(value = "Stripe-Signature", required = false) String stripeSignature,
            @RequestHeader(value = "X-UPI-Signature", required = false) String upiSignature,
            @RequestHeader(value = "X-Webhook-Signature", required = false) String genericSignature,
            @RequestParam(required = false) String providerTxnId,
            @RequestParam(required = false, defaultValue = "payment.succeeded") String eventType,
            @RequestBody(required = false) String rawPayload
    ) {
        String signature = razorpaySignature != null ? razorpaySignature
                : (stripeSignature != null ? stripeSignature
                : (upiSignature != null ? upiSignature : genericSignature));

        String txnId = providerTxnId;
        String resolvedEventType = eventType;

        if (rawPayload != null && !rawPayload.isBlank()) {
            try {
                JsonNode rootNode = OBJECT_MAPPER.readTree(rawPayload);
                if (txnId == null) {
                    if (rootNode.has("providerTxnId")) {
                        txnId = rootNode.get("providerTxnId").asText();
                    } else if (rootNode.has("id")) {
                        txnId = rootNode.get("id").asText();
                    }
                }
                if (rootNode.has("eventType")) {
                    resolvedEventType = rootNode.get("eventType").asText();
                } else if (rootNode.has("event")) {
                    resolvedEventType = rootNode.get("event").asText();
                }
            } catch (Exception e) {
                log.debug("Webhook raw payload is not JSON: {}", e.getMessage());
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
                resolvedEventType,
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

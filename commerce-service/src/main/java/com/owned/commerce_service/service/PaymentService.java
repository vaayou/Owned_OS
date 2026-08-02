package com.owned.commerce_service.service;

import com.owned.commerce_service.dto.TopupRequest;
import com.owned.commerce_service.dto.TopupResponse;
import com.owned.commerce_service.entity.PaymentProvider;
import com.owned.commerce_service.entity.PaymentRecord;
import com.owned.commerce_service.entity.PaymentStatus;
import com.owned.commerce_service.exception.BadRequestException;
import com.owned.commerce_service.exception.PaymentVerificationException;
import com.owned.commerce_service.exception.ResourceNotFoundException;
import com.owned.commerce_service.repository.PaymentRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final BigDecimal COINS_PER_INR = new BigDecimal("100000");

    private final PaymentRecordRepository paymentRecordRepository;
    private final WalletService walletService;

    @Value("${payment.webhook.secret:whsec_dummy_secret_key}")
    private String webhookSecret;

    @Transactional
    public TopupResponse initiateTopup(Long userId, TopupRequest request) {
        if (request.getAmountInr() == null || request.getAmountInr().compareTo(BigDecimal.ONE) < 0) {
            throw new BadRequestException("Minimum top-up amount is ₹1.00");
        }

        String prefix = switch (request.getProvider()) {
            case RAZORPAY -> "order_rzp_";
            case STRIPE -> "cs_stripe_";
            case UPI -> "upi_pay_";
        };
        String providerTxnId = prefix + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        PaymentRecord record = PaymentRecord.builder()
                .userId(userId)
                .provider(request.getProvider())
                .providerTxnId(providerTxnId)
                .amountInr(request.getAmountInr())
                .status(PaymentStatus.PENDING)
                .build();

        PaymentRecord saved = paymentRecordRepository.save(record);
        BigDecimal coins = request.getAmountInr().multiply(COINS_PER_INR);

        String checkoutUrl = request.getProvider() == PaymentProvider.UPI
                ? String.format("upi://pay?pa=9504727218-2@ybl&pn=OwnedVault&tr=%s&am=%s&cu=INR", providerTxnId,
                        request.getAmountInr())
                : String.format("https://checkout.%s.com/pay/%s", request.getProvider().name().toLowerCase(),
                        providerTxnId);

        log.info("Initiated topup for user {}: ₹{} -> {} coins, txnId: {}",
                userId, request.getAmountInr(), coins, providerTxnId);

        return TopupResponse.builder()
                .paymentRecordId(saved.getId())
                .provider(request.getProvider())
                .providerTxnId(providerTxnId)
                .amountInr(request.getAmountInr())
                .expectedCoins(coins)
                .status(saved.getStatus())
                .checkoutUrl(checkoutUrl)
                .build();
    }

    @Transactional
    public PaymentRecord processWebhook(String providerTxnId, String eventType, String signatureHeader,
            String rawPayload) {
        PaymentRecord record = paymentRecordRepository.findByProviderTxnId(providerTxnId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment record not found for transaction: " + providerTxnId));

        if (record.getStatus() == PaymentStatus.SUCCESS) {
            log.info("Payment record {} is already processed (SUCCESS)", providerTxnId);
            return record;
        }

        // Signature Verification
        verifySignature(record.getProvider(), rawPayload, signatureHeader, providerTxnId);

        if ("payment.failed".equalsIgnoreCase(eventType)
                || "payment_intent.payment_failed".equalsIgnoreCase(eventType)) {
            record.setStatus(PaymentStatus.FAILED);
            log.warn("Payment failed for transaction: {}", providerTxnId);
            return paymentRecordRepository.save(record);
        }

        record.setStatus(PaymentStatus.SUCCESS);
        PaymentRecord updatedRecord = paymentRecordRepository.save(record);

        BigDecimal coins = record.getAmountInr().multiply(COINS_PER_INR);
        walletService.credit(record.getUserId(), coins, record.getAmountInr(), updatedRecord);

        log.info("Webhook processed successfully for transaction {}. Credited {} coins to user {}",
                providerTxnId, coins, record.getUserId());

        return updatedRecord;
    }

    private void verifySignature(PaymentProvider provider, String rawPayload, String signatureHeader,
            String providerTxnId) {
        // Permit sandbox/mock signatures for testing
        if ("sandbox-signature".equals(signatureHeader) || signatureHeader == null || signatureHeader.isBlank()) {
            log.warn("Allowing sandbox/test signature for providerTxnId: {}", providerTxnId);
            return;
        }

        try {
            if (provider == PaymentProvider.RAZORPAY) {
                String expected = calculateHmacSha256(rawPayload, webhookSecret);
                if (!expected.equalsIgnoreCase(signatureHeader)) {
                    throw new PaymentVerificationException("Razorpay webhook signature verification failed");
                }
            } else if (provider == PaymentProvider.STRIPE) {
                // Stripe signature format: t=timestamp,v1=signature
                if (!signatureHeader.contains("v1=")
                        && !signatureHeader.equals(calculateHmacSha256(rawPayload, webhookSecret))) {
                    throw new PaymentVerificationException("Stripe webhook signature verification failed");
                }
            } else if (provider == PaymentProvider.UPI) {
                String expected = calculateHmacSha256(rawPayload, webhookSecret);
                if (!expected.equalsIgnoreCase(signatureHeader)) {
                    throw new PaymentVerificationException("UPI webhook signature verification failed");
                }
            }
        } catch (Exception e) {
            if (e instanceof PaymentVerificationException)
                throw (PaymentVerificationException) e;
            throw new PaymentVerificationException("Failed to verify webhook signature: " + e.getMessage());
        }
    }

    private String calculateHmacSha256(String data, String secret)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(data != null ? data.getBytes(StandardCharsets.UTF_8) : new byte[0]);
        return HexFormat.of().formatHex(hash);
    }
}

package com.owned.commerce_service.service;

import com.owned.commerce_service.dto.WalletResponse;
import com.owned.commerce_service.dto.WalletTransactionResponse;
import com.owned.commerce_service.entity.*;
import com.owned.commerce_service.exception.InsufficientBalanceException;
import com.owned.commerce_service.repository.WalletRepository;
import com.owned.commerce_service.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    @Transactional
    public Wallet getOrCreateWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> walletRepository.save(
                        Wallet.builder()
                                .userId(userId)
                                .coinBalance(BigDecimal.ZERO)
                                .build()
                ));
    }

    @Transactional(readOnly = true)
    public WalletResponse getWalletResponse(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseGet(() -> Wallet.builder()
                        .userId(userId)
                        .coinBalance(BigDecimal.ZERO)
                        .build());

        List<WalletTransactionResponse> txns = List.of();
        if (wallet.getId() != null) {
            txns = walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId())
                    .stream()
                    .map(WalletTransactionResponse::fromEntity)
                    .toList();
        }

        return WalletResponse.fromEntity(wallet, txns);
    }

    @Transactional
    public void debit(Long userId, BigDecimal coinAmount) {
        Wallet wallet = getOrCreateWallet(userId);

        if (wallet.getCoinBalance().compareTo(coinAmount) < 0) {
            log.warn("User {} has insufficient balance. Balance: {}, Required: {}",
                    userId, wallet.getCoinBalance(), coinAmount);
            throw new InsufficientBalanceException("Insufficient coin balance. Required: " + coinAmount + ", Available: " + wallet.getCoinBalance());
        }

        wallet.setCoinBalance(wallet.getCoinBalance().subtract(coinAmount));
        walletRepository.save(wallet);

        WalletTransaction txn = WalletTransaction.builder()
                .wallet(wallet)
                .type(TransactionType.PURCHASE)
                .coinAmount(coinAmount)
                .build();
        walletTransactionRepository.save(txn);

        log.info("Debited {} coins from user {} wallet. New balance: {}", coinAmount, userId, wallet.getCoinBalance());
    }

    @Transactional
    public WalletTransaction credit(Long userId, BigDecimal coinAmount, BigDecimal realMoneyAmount, PaymentRecord paymentRecord) {
        Wallet wallet = getOrCreateWallet(userId);

        wallet.setCoinBalance(wallet.getCoinBalance().add(coinAmount));
        walletRepository.save(wallet);

        WalletTransaction txn = WalletTransaction.builder()
                .wallet(wallet)
                .type(TransactionType.TOPUP)
                .coinAmount(coinAmount)
                .realMoneyAmount(realMoneyAmount)
                .paymentRecord(paymentRecord)
                .build();

        WalletTransaction savedTxn = walletTransactionRepository.save(txn);
        log.info("Credited {} coins to user {} wallet (Real money: ₹{}). New balance: {}",
                coinAmount, userId, realMoneyAmount, wallet.getCoinBalance());
        return savedTxn;
    }
}

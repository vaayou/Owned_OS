package com.owned.commerce_service.repository;

import com.owned.commerce_service.entity.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {
    Optional<PaymentRecord> findByProviderTxnId(String providerTxnId);
    List<PaymentRecord> findByUserIdOrderByCreatedAtDesc(Long userId);
}

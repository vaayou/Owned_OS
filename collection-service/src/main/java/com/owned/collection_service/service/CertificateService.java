package com.owned.collection_service.service;

import com.owned.collection_service.dto.CertificateResponse;
import com.owned.collection_service.entity.Certificate;
import com.owned.collection_service.entity.CollectionItem;
import com.owned.collection_service.repository.CertificateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;

    @Transactional
    public Certificate generateCertificate(CollectionItem item) {
        log.info("Generating certificate for collection item ID: {}", item.getId());

        // Serial number format: OWN-2026-000482
        String serialNo = String.format("OWN-2026-%06d", item.getId());
        String imageUrl = "/api/certificates/" + item.getId() + "/image";

        Certificate certificate = Certificate.builder()
                .collectionItem(item)
                .serialNo(serialNo)
                .imageUrl(imageUrl)
                .build();

        Certificate saved = certificateRepository.save(certificate);
        log.info("Certificate generated successfully. Serial: {}, ID: {}", saved.getSerialNo(), saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public CertificateResponse getCertificateById(Long id) {
        Certificate cert = certificateRepository.findById(id)
                .or(() -> certificateRepository.findByCollectionItemId(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate not found for ID: " + id));

        return CertificateResponse.fromEntity(cert);
    }

    @Transactional(readOnly = true)
    public String generateCertificateSvg(Long id) {
        Certificate cert = certificateRepository.findById(id)
                .or(() -> certificateRepository.findByCollectionItemId(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate not found for ID: " + id));

        CollectionItem item = cert.getCollectionItem();
        String formattedDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss UTC")
                .withZone(ZoneId.of("UTC"))
                .format(cert.getGeneratedAt() != null ? cert.getGeneratedAt() : Instant.now());

        String productName = item.getProductNameSnapshot() != null ? item.getProductNameSnapshot() : "Item";
        String price = item.getPriceCoinsSnapshot() != null ? item.getPriceCoinsSnapshot().toPlainString() : "0";

        return """
            <svg xmlns="http://www.w3.org/2000/svg" width="800" height="500" viewBox="0 0 800 500">
                <defs>
                    <linearGradient id="bg" x1="0%%" y1="0%%" x2="100%%" y2="100%%">
                        <stop offset="0%%" stop-color="#0f172a"/>
                        <stop offset="50%%" stop-color="#1e1b4b"/>
                        <stop offset="100%%" stop-color="#311042"/>
                    </linearGradient>
                    <linearGradient id="gold" x1="0%%" y1="0%%" x2="100%%" y2="0%%">
                        <stop offset="0%%" stop-color="#f59e0b"/>
                        <stop offset="50%%" stop-color="#fbbf24"/>
                        <stop offset="100%%" stop-color="#d97706"/>
                    </linearGradient>
                </defs>

                <!-- Background Card -->
                <rect width="800" height="500" rx="20" fill="url(#bg)" stroke="url(#gold)" stroke-width="4"/>

                <!-- Header Title -->
                <text x="400" y="70" text-anchor="middle" font-family="'Outfit', sans-serif" font-size="28" font-weight="bold" fill="url(#gold)" letter-spacing="4">
                    CERTIFICATE OF OWNERSHIP
                </text>
                <line x1="150" y1="90" x2="650" y2="90" stroke="url(#gold)" stroke-width="2" stroke-dasharray="6,4"/>

                <!-- Subtitle -->
                <text x="400" y="130" text-anchor="middle" font-family="sans-serif" font-size="16" fill="#94a3b8">
                    This document officially certifies that User #%d is the verified owner of:
                </text>

                <!-- Product Name Box -->
                <rect x="150" y="160" width="500" height="80" rx="12" fill="#1e293b" stroke="#334155" stroke-width="1.5"/>
                <text x="400" y="210" text-anchor="middle" font-family="sans-serif" font-size="24" font-weight="bold" fill="#ffffff">
                    %s
                </text>

                <!-- Details Grid -->
                <text x="200" y="290" font-family="sans-serif" font-size="14" fill="#94a3b8">SERIAL NO:</text>
                <text x="200" y="315" font-family="monospace" font-size="18" font-weight="bold" fill="#38bdf8">%s</text>

                <text x="500" y="290" font-family="sans-serif" font-size="14" fill="#94a3b8">ACQUISITION VALUE:</text>
                <text x="500" y="315" font-family="sans-serif" font-size="18" font-weight="bold" fill="#f59e0b">%s COINS</text>

                <!-- Footer info -->
                <line x1="100" y1="380" x2="700" y2="380" stroke="#334155" stroke-width="1"/>

                <text x="400" y="420" text-anchor="middle" font-family="sans-serif" font-size="12" fill="#64748b">
                    Generated on %s • Order #%d • OWNED Microservice Protocol
                </text>
            </svg>
            """.formatted(
                item.getUserId(),
                escapeXml(productName),
                cert.getSerialNo(),
                price,
                formattedDate,
                item.getOrderId()
        );
    }

    private String escapeXml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&apos;");
    }
}

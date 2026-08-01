package com.owned.collection_service.controller;

import com.owned.collection_service.dto.CertificateResponse;
import com.owned.collection_service.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    /**
     * GET /certificates/{id}
     * Returns detail JSON metadata for a certificate
     */
    @GetMapping("/{id}")
    public ResponseEntity<CertificateResponse> getCertificateDetail(@PathVariable Long id) {
        return ResponseEntity.ok(certificateService.getCertificateById(id));
    }

    /**
     * GET /certificates/{id}/image
     * Renders server-side SVG certificate graphic image
     */
    @GetMapping("/{id}/image")
    public ResponseEntity<String> getCertificateImage(@PathVariable Long id) {
        String svgContent = certificateService.generateCertificateSvg(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/svg+xml")
                .body(svgContent);
    }
}

package com.owned.commerce_service.controller;

import com.owned.commerce_service.dto.ProductResponse;
import com.owned.commerce_service.entity.Tier;
import com.owned.commerce_service.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final CatalogService catalogService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Tier tier,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(catalogService.getProducts(categoryId, tier, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getProductById(id));
    }
}

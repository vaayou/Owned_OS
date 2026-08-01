package com.owned.commerce_service.service;

import com.owned.commerce_service.dto.CategoryResponse;
import com.owned.commerce_service.dto.ProductResponse;
import com.owned.commerce_service.entity.Category;
import com.owned.commerce_service.entity.Product;
import com.owned.commerce_service.entity.Tier;
import com.owned.commerce_service.exception.ResourceNotFoundException;
import com.owned.commerce_service.repository.CategoryRepository;
import com.owned.commerce_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<ProductResponse> getProducts(Long categoryId, Tier tier, String search) {
        List<Product> products;
        if (categoryId != null || tier != null || (search != null && !search.isBlank())) {
            products = productRepository.filterProducts(categoryId, tier, search != null && !search.isBlank() ? search.trim() : null);
        } else {
            products = productRepository.findByIsActiveTrue();
        }
        return products.stream().map(ProductResponse::fromEntity).toList();
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return ProductResponse.fromEntity(product);
    }

    public List<CategoryResponse> getCategoryTree() {
        List<Category> rootCategories = categoryRepository.findByParentIsNull();
        return rootCategories.stream().map(CategoryResponse::fromEntity).toList();
    }
}

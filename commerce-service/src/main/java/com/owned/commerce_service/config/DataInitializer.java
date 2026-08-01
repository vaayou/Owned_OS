package com.owned.commerce_service.config;

import com.owned.commerce_service.entity.Category;
import com.owned.commerce_service.entity.Product;
import com.owned.commerce_service.entity.Tier;
import com.owned.commerce_service.repository.CategoryRepository;
import com.owned.commerce_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (categoryRepository.count() > 0) {
            log.info("Categories and products already initialized.");
            return;
        }

        log.info("Initializing sample Categories (Everyday, Luxury, Ultra) and Products...");

        // Root Categories
        Category everyday = Category.builder()
                .name("Everyday Essentials")
                .tier(Tier.EVERYDAY)
                .build();

        Category luxury = Category.builder()
                .name("Luxury Collection")
                .tier(Tier.LUXURY)
                .build();

        Category ultra = Category.builder()
                .name("Ultra Exclusive")
                .tier(Tier.ULTRA)
                .build();

        categoryRepository.saveAll(List.of(everyday, luxury, ultra));

        // Subcategories
        Category streetwear = Category.builder()
                .name("Streetwear & Tees")
                .tier(Tier.EVERYDAY)
                .parent(everyday)
                .build();

        Category timepieces = Category.builder()
                .name("Luxury Timepieces")
                .tier(Tier.LUXURY)
                .parent(luxury)
                .build();

        Category supercars = Category.builder()
                .name("Hypercars & Estates")
                .tier(Tier.ULTRA)
                .parent(ultra)
                .build();

        categoryRepository.saveAll(List.of(streetwear, timepieces, supercars));

        // Products
        Product hoodie = Product.builder()
                .category(streetwear)
                .name("Owned Signature Hoodie")
                .description("Heavyweight 450gsm organic cotton oversized black hoodie.")
                .imageUrl("https://images.unsplash.com/photo-1556905055-8f358a7a47b2")
                .priceCoins(new BigDecimal("500"))
                .isActive(true)
                .build();

        Product watch = Product.builder()
                .category(timepieces)
                .name("Chronograph Gold Edition")
                .description("Automatic mechanical movement with 18k rose gold bezel.")
                .imageUrl("https://images.unsplash.com/photo-1523275335684-37898b6baf30")
                .priceCoins(new BigDecimal("5000"))
                .isActive(true)
                .build();

        Product car = Product.builder()
                .category(supercars)
                .name("Owned Founder Hypercar Experience")
                .description("V12 Quad-Turbo prototype access membership & track key.")
                .imageUrl("https://images.unsplash.com/photo-1503376780353-7e6692767b70")
                .priceCoins(new BigDecimal("50000"))
                .isActive(true)
                .build();

        productRepository.saveAll(List.of(hoodie, watch, car));

        log.info("Data initialization complete. Seeded 6 categories and 3 products.");
    }
}

package com.owned.commerce_service.repository;

import com.owned.commerce_service.entity.Category;
import com.owned.commerce_service.entity.Tier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNull();
    List<Category> findByTier(Tier tier);
}

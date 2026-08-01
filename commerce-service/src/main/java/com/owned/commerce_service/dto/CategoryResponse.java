package com.owned.commerce_service.dto;

import com.owned.commerce_service.entity.Category;
import com.owned.commerce_service.entity.Tier;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {

    private Long id;
    private String name;
    private Tier tier;
    private Long parentId;
    private List<CategoryResponse> children;

    public static CategoryResponse fromEntity(Category category) {
        if (category == null) return null;
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .tier(category.getTier())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .children(category.getChildren() != null
                        ? category.getChildren().stream().map(CategoryResponse::fromEntity).toList()
                        : List.of())
                .build();
    }
}

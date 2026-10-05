package com.example.library.mapper.category;

import com.example.library.dto.response.category.CategoryResponse;
import com.example.library.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(),
                category.getDescription(), category.isActive());
    }
}

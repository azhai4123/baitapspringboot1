package com.example.library.service.category;

import com.example.library.dto.request.category.CategoryCreateRequest;
import com.example.library.dto.response.category.CategoryResponse;
import com.example.library.entity.Category;
import com.example.library.exception.CategoryAlreadyExistsException;
import com.example.library.exception.CategoryNotFoundException;
import com.example.library.dto.request.category.CategoryUpdateRequest;
import com.example.library.mapper.category.CategoryMapper;
import com.example.library.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper mapper) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> search(String query, boolean includeInactive, Pageable pageable) {
        return categoryRepository.search(normalize(query), includeInactive, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CategoryResponse get(Long id) {
        return mapper.toResponse(find(id));
    }

    @Transactional
    public CategoryResponse create(CategoryCreateRequest request) {
        ensureNameAvailable(request.getName(), null);
        return mapper.toResponse(categoryRepository.save(
                new Category(request.getName().trim(), trimToNull(request.getDescription()))));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryUpdateRequest request) {
        Category category = find(id);
        ensureNameAvailable(request.getName(), id);
        category.setName(request.getName().trim());
        category.setDescription(trimToNull(request.getDescription()));
        return mapper.toResponse(category);
    }

    @Transactional
    public void deactivate(Long id) {
        Category category = find(id);
        category.setActive(false);
    }

    private Category find(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
    }

    private void ensureNameAvailable(String name, Long id) {
        boolean exists = id == null ? categoryRepository.existsByNameIgnoreCase(name.trim())
                : categoryRepository.existsByNameIgnoreCaseAndIdNot(name.trim(), id);
        if (exists) throw new CategoryAlreadyExistsException(name.trim());
    }

    private static String normalize(String value) { return value == null ? "" : value.trim(); }
    private static String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

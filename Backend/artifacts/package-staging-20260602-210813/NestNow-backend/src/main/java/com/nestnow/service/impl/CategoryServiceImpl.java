package com.nestnow.service.impl;

import com.nestnow.dto.category.CategoryResponse;
import com.nestnow.dto.category.CreateCategoryRequest;
import com.nestnow.entity.Category;
import com.nestnow.exception.ResourceAlreadyExistsException;
import com.nestnow.repository.CategoryRepository;
import com.nestnow.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl
        implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse createCategory(
            CreateCategoryRequest request
    ) {

        if (categoryRepository.existsByName(request.getName())) {

            throw new ResourceAlreadyExistsException(
                    "Category already exists"
            );
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        categoryRepository.save(category);

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    @Override
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(category -> CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .description(category.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}
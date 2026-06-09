package com.nestnow.service;

import com.nestnow.dto.category.CategoryResponse;
import com.nestnow.dto.category.CreateCategoryRequest;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(
            CreateCategoryRequest request
    );

    List<CategoryResponse> getAllCategories();
}
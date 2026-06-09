package com.nestnow.controller;

import com.nestnow.dto.category.CategoryResponse;
import com.nestnow.dto.category.CreateCategoryRequest;
import com.nestnow.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    public CategoryResponse createCategory(
            @RequestBody CreateCategoryRequest request
    ) {

        return categoryService.createCategory(request);
    }

    @GetMapping
    public List<CategoryResponse> getAllCategories() {

        return categoryService.getAllCategories();
    }
}
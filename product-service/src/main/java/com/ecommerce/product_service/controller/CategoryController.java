package com.ecommerce.product_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.product_service.dto.CategoryDto.CategoryRequest;
import com.ecommerce.product_service.dto.CategoryDto.CategoryResponse;
import com.ecommerce.product_service.dto.CategoryDto.CategorySummary;
import com.ecommerce.product_service.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;

	@PostMapping
	public ResponseEntity<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
	}

	@GetMapping("/{id}")
	public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
		return ResponseEntity.ok(categoryService.getCategoryById(id));
	}

	@GetMapping("/slug/{slug}")
	public ResponseEntity<CategoryResponse> getCategoryBySlug(@PathVariable String slug) {
		return ResponseEntity.ok(categoryService.getCategoryBySlug(slug));
	}

	@GetMapping("/roots")
	public ResponseEntity<List<CategorySummary>> getRootCategories() {
		return ResponseEntity.ok(categoryService.getRootCategories());
	}

	@GetMapping("/{parentId}/subcategories")
	public ResponseEntity<List<CategorySummary>> getSubCategories(@PathVariable Long parentId) {
		return ResponseEntity.ok(categoryService.getSubCategories(parentId));
	}

	@PutMapping("/{id}")
	public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id,
			@RequestBody @Valid CategoryRequest request) {
		return ResponseEntity.ok(categoryService.updateCategory(id, request));
	}

}

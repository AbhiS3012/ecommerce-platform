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

import com.ecommerce.product_service.dto.ProductDto.ProductRequest;
import com.ecommerce.product_service.dto.ProductDto.ProductResponse;
import com.ecommerce.product_service.dto.ProductDto.ProductSummary;
import com.ecommerce.product_service.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@PostMapping
	public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
		return ResponseEntity.ok(productService.getProductById(id));
	}

	@GetMapping("/slug/{slug}")
	public ResponseEntity<ProductResponse> getProductById(@PathVariable String slug) {
		return ResponseEntity.ok(productService.getProductBySlug(slug));
	}
	
	@GetMapping
	public ResponseEntity<List<ProductSummary>> getAllProducts() {
		return ResponseEntity.ok(productService.getAllProducts());
	}
	
	@GetMapping("/category/{categoryId}")
	public ResponseEntity<List<ProductSummary>> getProductsByCategory(@PathVariable Long categoryId) {
		return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductRequest request) {
		return ResponseEntity.ok(productService.updateProduct(id, request));
	}

}

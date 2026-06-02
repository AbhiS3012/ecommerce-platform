package com.ecommerce.product_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.product_service.dto.ProductDto.ProductResponse;
import com.ecommerce.product_service.entity.Product.Category;
import com.ecommerce.product_service.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@GetMapping
	public ResponseEntity<List<ProductResponse>> getAllActiveProducts() {
		return ResponseEntity.ok(productService.getAllActiveProducts());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductResponse> getActiveProductById(@PathVariable Long id) {
		return ResponseEntity.ok(productService.getActiveProductById(id));
	}

	@GetMapping("/category/{category}")
	public ResponseEntity<List<ProductResponse>> getActiveProductsByCategory(@PathVariable Category category) {
		return ResponseEntity.ok(productService.getActiveProductsByCategory(category));
	}

}

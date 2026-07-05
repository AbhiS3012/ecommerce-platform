package com.ecommerce.product_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.product_service.dto.ProductDto.ProductRequest;
import com.ecommerce.product_service.dto.ProductDto.ProductResponse;
import com.ecommerce.product_service.dto.ProductDto.ProductSummary;
import com.ecommerce.product_service.entity.Category;
import com.ecommerce.product_service.entity.Product;
import com.ecommerce.product_service.exception.ResourceNotFoundException;
import com.ecommerce.product_service.repository.CategoryRepository;
import com.ecommerce.product_service.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final SlugService slugService;
	
	@Transactional
	public ProductResponse createProduct(ProductRequest request) {
		
		Category category = categoryRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException("Category not found"));
		
		//auto generate slug from name
		String slug = slugService.generateUniqueSlug(request.getName(), productRepository::findSlugsStartsWith);
		
		Product product = Product.builder()
				.name(request.getName())
				.description(request.getDescription())
				.slug(slug)
				.active(request.isActive())
				.category(category)
				.build();
		
		return toResponse(productRepository.save(product));
	}
	
	public ProductResponse getProductById(Long id) {
		Product product = productRepository.findByIdWithCategory(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found"));
		return toResponse(product);
	}
	
	public ProductResponse getProductBySlug(String slug) {
		Product product = productRepository.findBySlugWithCategory(slug)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found"));
		return toResponse(product);
	}
	
	public List<ProductSummary> getAllProducts() {
		return productRepository.findAllWithCategory().stream().map(this::toSummary).collect(Collectors.toList());
	}

	public List<ProductSummary> getProductsByCategory(Long categoryId) {
		categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

		return productRepository.findByCategoryId(categoryId).stream().map(this::toSummary)
				.collect(Collectors.toList());
	}
	
	public ProductResponse updateProduct(Long id, ProductRequest request) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found"));

		// update slug only if name changed
		if (!request.getName().equals(product.getName())) {
			String slug = slugService.generateUniqueSlug(request.getName(), productRepository::findSlugsStartsWith);
			product.setSlug(slug);
		}

		// update category only if changed
		if (!product.getCategory().getId().equals(request.getCategoryId())) {
			Category category = categoryRepository.findById(request.getCategoryId())
					.orElseThrow(() -> new ResourceNotFoundException("Category not found"));
			product.setCategory(category);
		}

		product.setName(request.getName());
		product.setDescription(request.getDescription());
		product.setActive(request.isActive());

		return toResponse(productRepository.save(product));
	}

	
	//=============== Helper functions =================
	
	private ProductResponse toResponse(Product product) {
		Category category = product.getCategory();
		
		return ProductResponse.builder()
				.id(product.getId())
				.name(product.getName())
				.description(product.getDescription())
				.slug(product.getSlug())
				.active(product.isActive())
				.categoryId(category != null ? category.getId() : null)
				.categoryName(category != null ? category.getName() : null)
				.categorySlug(category != null ? category.getSlug() : null)
				.build();
	}
	
	private ProductSummary toSummary(Product product) {
		Category category = product.getCategory();
		
		return ProductSummary.builder()
				.id(product.getId())
				.name(product.getName())
				.slug(product.getSlug())
				.active(product.isActive())
				.categoryId(category != null ? category.getId() : null)
				.categoryName(category != null ? category.getName() : null)
				.build();
	}
	
}

package com.ecommerce.product_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ecommerce.product_service.dto.ProductDto;
import com.ecommerce.product_service.entity.Product;
import com.ecommerce.product_service.exception.ProductAlredyExistsException;
import com.ecommerce.product_service.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;

	public void createProduct(ProductDto.CreateRequest request) {
		if (productRepository.existsByNameAndCategory(request.getName(), request.getCategory())) {
			throw new ProductAlredyExistsException("Product already exists in this category: " + request.getName());
		}

		Product product = Product.builder()
				.name(request.getName())
				.category(request.getCategory())
				.description(request.getDescription())
				.price(request.getPrice())
				.stock(request.getStock())
				.build();
		
		productRepository.save(product);
	}
	
	public List<ProductDto.ProductResponse> getAllProducts() {
		return productRepository.findByActiveTrue()
				.stream()
				.map(this::toResponse)
				.collect(Collectors.toList());
	}
	
	public ProductDto.ProductResponse getProductById(Long id) {
		Product product = productRepository.findByIdAndActiveTrue(id);
		return toResponse(product);
	}
	
	public List<ProductDto.ProductResponse> getProductsByCategory(Product.Category category) {
		return productRepository.findByCategoryAndActiveTrue(category)
				.stream()
				.map(this::toResponse)
				.collect(Collectors.toList());
	}
	
	public void updateProduct(Long id, ProductDto.UpdateRequest request) {
		Product product = productRepository.findByIdAndActiveTrue(id);
		if(request.getDescription() != null) product.setDescription(request.getDescription());
		if(request.getPrice() != null) product.setPrice(request.getPrice());
		if(request.getStock() != null) product.setStock(request.getStock());
		productRepository.save(product);
	}
	
	public void deleteProduct(Long id) {
		Product product = productRepository.findByIdAndActiveTrue(id);
		product.setActive(false);
		productRepository.save(product);
	}
	
	private ProductDto.ProductResponse toResponse(Product product) {
		return ProductDto.ProductResponse.builder()
				.id(product.getId())
				.name(product.getName())
				.category(product.getCategory())
				.description(product.getDescription())
				.price(product.getPrice())
				.stock(product.getStock())
				.active(product.isActive())
				.createdAt(product.getCreatedAt())
				.build();
	}

}

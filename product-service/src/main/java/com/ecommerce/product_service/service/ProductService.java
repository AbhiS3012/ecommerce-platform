package com.ecommerce.product_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.product_service.dto.ProductDto;
import com.ecommerce.product_service.dto.ProductDto.ProductResponse;
import com.ecommerce.product_service.entity.Product;
import com.ecommerce.product_service.entity.Product.Status;
import com.ecommerce.product_service.exception.ProductAlredyExistsException;
import com.ecommerce.product_service.exception.ProductNotFoundException;
import com.ecommerce.product_service.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;

	@Transactional
	public ProductResponse createProduct(ProductDto.CreateRequest request) {
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
		
		return toResponse(productRepository.save(product));
	}
	
	public List<ProductResponse> getAllActiveProducts() {
		return productRepository.findByStatus(Status.ACTIVE)
				.stream()
				.map(this::toResponse)
				.collect(Collectors.toList());
	}
	
	public List<ProductResponse> getAllProducts() {
		return productRepository.findAll()
				.stream()
				.map(this::toResponse)
				.collect(Collectors.toList());
	}
	
	public ProductResponse getActiveProductById(Long id) {
		Product product = productRepository.findByIdAndStatus(id, Status.ACTIVE)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
		return toResponse(product);
	}
	
	public ProductResponse getProductById(Long id) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
		return toResponse(product);
	}
	
	public List<ProductResponse> getActiveProductsByCategory(Product.Category category) {
		return productRepository.findByCategoryAndStatus(category, Status.ACTIVE)
				.stream()
				.map(this::toResponse)
				.collect(Collectors.toList());
	}
	
	public List<ProductResponse> getAllProductsByCategory(Product.Category category) {
		return productRepository.findByCategory(category)
				.stream()
				.map(this::toResponse)
				.collect(Collectors.toList());
	}
	
	@Transactional
	public ProductDto.ProductResponse updateProduct(Long id, ProductDto.UpdateRequest request) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
		
		if (product.getStatus().equals(Status.DISCONTINUED)) {
	        throw new IllegalStateException("Discontinued product cannot be updated");
	    }
		
		if(request.getDescription() != null) product.setDescription(request.getDescription());
		if(request.getPrice() != null) product.setPrice(request.getPrice());
		if(request.getStock() != null) product.setStock(request.getStock());
		return toResponse(productRepository.save(product));
	}
	
	@Transactional
	public void deactivateProduct(Long id) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
		
		if (product.getStatus().equals(Status.DISCONTINUED)) {
	        throw new IllegalStateException("Discontinued product cannot be deactivate");
	    }
		
		product.setStatus(Status.INACTIVE);
		productRepository.save(product);
	}
	
	@Transactional
	public void activateProduct(Long id) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
		
		if (product.getStatus().equals(Status.DISCONTINUED)) {
	        throw new IllegalStateException("Discontinued product cannot be activate");
	    }
		
		product.setStatus(Status.ACTIVE);
		productRepository.save(product);
	}
	
	@Transactional
	public void deleteProduct(Long id) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
		product.setStatus(Status.DISCONTINUED);
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
				.status(product.getStatus())
				.createdAt(product.getCreatedAt())
				.build();
	}

}

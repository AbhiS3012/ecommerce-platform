package com.ecommerce.product_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.ecommerce.product_service.dto.ProductDto.ProductRequest;
import com.ecommerce.product_service.dto.ProductDto.ProductResponse;
import com.ecommerce.product_service.entity.Category;
import com.ecommerce.product_service.entity.Product;
import com.ecommerce.product_service.exception.ResourceNotFoundException;
import com.ecommerce.product_service.repository.CategoryRepository;
import com.ecommerce.product_service.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Tests")
public class ProductserviceTest {

	@Mock
	private ProductRepository productRepository;
	@Mock
	private CategoryRepository categoryRepository;
	@Mock
	private SlugService slugService;

	@InjectMocks
	private ProductService productService;
	
	private Category category;
	private ProductRequest request;
	private Product product;
	
	@BeforeEach
	void setUp() {
		category = Category.builder()
				.id(1L)
				.name("Electronics")
				.slug("electronics")
				.build();
		
		request = ProductRequest.builder()
				.name("boAt Rockerz 450")
				.description("Bluetooth headphone")
				.categoryId(1L)
				.build();
		
		product = Product.builder()
				.name("boAt Rockerz 450")
				.description("Bluetooth headphone")
				.slug("boat-rockerz-450")
				.active(true)
				.category(category)
				.build();
		ReflectionTestUtils.setField(product, "id", 1L);
	}
	
	@Test
	@DisplayName("Should create product successfully")
	void shouldCreateProductSuccessfully() {
		//ARRANGE
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
		when(slugService.generateUniqueSlug(eq("boAt Rockerz 450"), any())).thenReturn("boat-rockerz-450");
		when(productRepository.save(any(Product.class))).thenReturn(product);
		
		//ACT
		ProductResponse response = productService.createProduct(request);
		
		//ASSERT
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(1L);
		assertThat(response.getName()).isEqualTo("boAt Rockerz 450");
		assertThat(response.getDescription()).isEqualTo("Bluetooth headphone");
		assertThat(response.getSlug()).isEqualTo("boat-rockerz-450");
		assertThat(response.getCategoryId()).isEqualTo(1L);
		assertThat(response.getCategoryName()).isEqualTo("Electronics");
		assertThat(response.getCategorySlug()).isEqualTo("electronics");
		
		verify(categoryRepository, times(1)).findById(anyLong());
		verify(productRepository, times(1)).save(any(Product.class));
	}
	
	@Test
	@DisplayName("Should throw ResourceNotFoundException when category not found")
	void shouldThrowExceptionWhenCategoryNotFound() {

		when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

		ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
				() -> productService.createProduct(request));
		
		assertThat(ex.getMessage()).isEqualTo("Category not found");
		
		// verify save never called
		verify(productRepository, never()).save(any());
	}
	
	@Test
	@DisplayName("Should return product when found by id")
	void shouldReturnProductWhenFoundById() {

		when(productRepository.findByIdWithCategory(1L)).thenReturn(Optional.of(product));

		ProductResponse response = productService.getProductById(1L);

		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(1L);
		assertThat(response.getName()).isEqualTo("boAt Rockerz 450");
		assertThat(response.getDescription()).isEqualTo("Bluetooth headphone");
		assertThat(response.getSlug()).isEqualTo("boat-rockerz-450");
		assertThat(response.getCategoryId()).isEqualTo(1L);
		assertThat(response.getCategoryName()).isEqualTo("Electronics");
		assertThat(response.getCategorySlug()).isEqualTo("electronics");
	}
	
	@Test
	@DisplayName("Should throw ResourceNotFoundException when product not found by id")
	void shouldThrowExceptionWhenProductNotFoundById() {

		when(productRepository.findByIdWithCategory(999L)).thenReturn(Optional.empty());

		ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
				() -> productService.getProductById(999L));

		assertThat(ex.getMessage()).isEqualTo("Product not found");
	}
	
	@Test
	@DisplayName("Should update product without changing slug or category")
	void shouldUpdateProductWithoutChangingSlugOrCategory() {
		
		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		when(productRepository.save(any(Product.class))).thenReturn(product);
		
		ProductResponse response = productService.updateProduct(1L, request);
		
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(1L);
		assertThat(response.getName()).isEqualTo("boAt Rockerz 450");
		
		verify(slugService, never()).generateUniqueSlug(anyString(), any());
		verify(categoryRepository, never()).findById(anyLong());
	}
	
	@Test
	@DisplayName("Should update product successfully")
	void shouldUpdateProductSuccessfully() {
		ProductRequest updateRequest = ProductRequest.builder()
				.name("boAt Rockerz 750")
				.description("Wired headphone")
				.categoryId(1L)
				.build();
		
		Product updatedProduct = Product.builder()
				.id(1L)
				.name("boAt Rockerz 750")
				.description("Wired headphone")
				.slug("boat-rockerz-750")
				.active(true)
				.category(category)
				.build();
		
		when(productRepository.findById(1L)).thenReturn(Optional.of(this.product));
		when(slugService.generateUniqueSlug(eq("boAt Rockerz 750"), any())).thenReturn("boat-rockerz-750");
		when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
		
		ProductResponse response = productService.updateProduct(1L, updateRequest);
		
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(1L);
		assertThat(response.getName()).isEqualTo("boAt Rockerz 750");
		assertThat(response.getDescription()).isEqualTo("Wired headphone");
		assertThat(response.getSlug()).isEqualTo("boat-rockerz-750");
		assertThat(response.getCategoryId()).isEqualTo(1L);
		assertThat(response.getCategoryName()).isEqualTo("Electronics");
		assertThat(response.getCategorySlug()).isEqualTo("electronics");
		
		verify(categoryRepository, never()).findById(anyLong());
	}
	
	@Test
	@DisplayName("Should update product category when category changed")
	void shouldUpdateProductCategoryWhenChanged() {

	    Category newCategory = Category.builder()
	            .name("Audio")
	            .slug("audio")
	            .build();
	    ReflectionTestUtils.setField(newCategory, "id", 2L);

	    ProductRequest updateRequest = ProductRequest.builder()
	            .name("boAt Rockerz 450")
	            .description("Bluetooth headphone")
	            .categoryId(2L)
	            .build();

	    Product updatedProduct = Product.builder()
	            .name("boAt Rockerz 450")
	            .slug("boat-rockerz-450")
	            .description("Bluetooth headphone")
	            .active(true)
	            .category(newCategory)
	            .build();
	    ReflectionTestUtils.setField(updatedProduct, "id", 1L);

		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		when(categoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
		when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

	    ProductResponse response = productService.updateProduct(1L, updateRequest);

	    assertThat(response.getCategoryId()).isEqualTo(2L);
	    assertThat(response.getCategoryName()).isEqualTo("Audio");

	    verify(slugService, never()).generateUniqueSlug(anyString(), any());
	    verify(categoryRepository, times(1)).findById(2L);
	}
	
}

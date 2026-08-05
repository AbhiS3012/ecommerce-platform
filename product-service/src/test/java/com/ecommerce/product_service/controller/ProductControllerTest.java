package com.ecommerce.product_service.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ecommerce.product_service.dto.ProductDto.ProductRequest;
import com.ecommerce.product_service.dto.ProductDto.ProductResponse;
import com.ecommerce.product_service.dto.ProductDto.ProductSummary;
import com.ecommerce.product_service.exception.ResourceNotFoundException;
import com.ecommerce.product_service.security.JwtAuthenticationFilter;
import com.ecommerce.product_service.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ProductController.class)
@DisplayName("ProductController Tests")
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTest {
	
	@MockitoBean
	private JwtAuthenticationFilter authenticationFilter;
	
	@MockitoBean
	private ProductService productService;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private ProductResponse productResponse;
	private ProductRequest productRequest;
	
	@BeforeEach
	void setUp() {
		productRequest = new ProductRequest("boAt Rockerz 450", "Bluetooth headphone", true, 4L);
		
		productResponse = ProductResponse.builder()
				.id(1L)
				.name("boAt Rockerz 450")
				.description("Bluetooth headphone")
				.slug("boat-rockerz-450")
				.active(true)
				.categoryId(4L)
				.categoryName("Headphones")
				.categorySlug("headphones")
				.build();
	}
	
	// ─── POST /api/v1/products ─────────────────────────────────────
	
	@Nested
	@DisplayName("POST /api/v1/products - create product tests")
	class CreateProductTest {
		
		@Test
		@DisplayName("Should create product and return 201")
		void shouldCreateProductAndReturn201() throws JsonProcessingException, Exception {
			when(productService.createProduct(any(ProductRequest.class))).thenReturn(productResponse);
			
			mockMvc.perform(
					post("/api/v1/products")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(productRequest))
				)
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.name").value("boAt Rockerz 450"))
			.andExpect(jsonPath("$.description").value("Bluetooth headphone"))
			.andExpect(jsonPath("$.slug").value("boat-rockerz-450"))
			.andExpect(jsonPath("$.active").value(true))
			.andExpect(jsonPath("$.categoryId").value(4L))
			.andExpect(jsonPath("$.categoryName").value("Headphones"))
			.andExpect(jsonPath("$.categorySlug").value("headphones"));
			
			verify(productService, times(1)).createProduct(any(ProductRequest.class));
		}
		
		@Test
		@DisplayName("Should return 400 when name is blank and category is null")
		void shouldReturn400WhenNameIsBlankAndCategoryIsNull() throws JsonProcessingException, Exception {
			ProductRequest productRequest = new ProductRequest("", "Bluetooth headphone", true, null);
			
			mockMvc.perform(
					post("/api/v1/products")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(productRequest))
				)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.message").exists());
			
			verify(productService, never()).createProduct(any());
		}
	}
	
	// ─── GET /api/v1/products/{id} ─────────────────────────────────────
	
	@Nested
	@DisplayName("GET /api/v1/products/{id} — get by id tests")
	class GetProductByIdTest {
		
		@Test
		@DisplayName("Should return product when find by id")
		void shouldReturnProductWhenFindById() throws Exception {
			when(productService.getProductById(1L)).thenReturn(productResponse);
			
			mockMvc.perform(
					get("/api/v1/products/1")
				)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.name").value("boAt Rockerz 450"))
			.andExpect(jsonPath("$.active").value(true))
			.andExpect(jsonPath("$.categoryId").value(4L));
			
			verify(productService, times(1)).getProductById(1L);
		}
		
		@Test
		@DisplayName("Should return 404 when product not found by id")
		void shouldReturn404WhenProductNotFoundById() throws Exception {
			when(productService.getProductById(999L)).thenThrow(new ResourceNotFoundException("Product not found"));
			
			mockMvc.perform(
					get("/api/v1/products/999")
				)
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value(404))
			.andExpect(jsonPath("$.message").value("Product not found"));
		}
	}
	
	// ─── GET /api/v1/products/slug/{slug} ─────────────────────────────────────
	
    @Nested
    @DisplayName("GET /api/v1/products/slug/{slug} — get by slug tests")
    class GetProductBySlugTest {
    	
    	@Test
    	@DisplayName("Should return product when find by slug")
    	void shouldReturnProductWhenFindBySlug() throws Exception {
    		when(productService.getProductBySlug("boat-rockerz-450")).thenReturn(productResponse);
    		
    		mockMvc.perform(
    				get("/api/v1/products/slug/boat-rockerz-450")
    			)
    		.andExpect(status().isOk())
    		.andExpect(jsonPath("$.id").value(1L))
    		.andExpect(jsonPath("$.slug").value("boat-rockerz-450"))
    		.andExpect(jsonPath("$.categoryId").value(4L));
    		
    		verify(productService, times(1)).getProductBySlug("boat-rockerz-450");
    	}
    	
    	@Test
    	@DisplayName("Should return 404 when product not found by slug")
    	void shouldReturn404WhenProductNotFoundBySlug() throws Exception {
    		when(productService.getProductBySlug("boat-rockerz-350")).thenThrow(new ResourceNotFoundException("Product not found"));
    		
    		mockMvc.perform(
    				get("/api/v1/products/slug/boat-rockerz-350")
    			)
    		.andExpect(status().isNotFound())
    		.andExpect(jsonPath("$.status").value(404))
    		.andExpect(jsonPath("$.message").value("Product not found"));
    	}
    }
	
	// ─── GET /api/v1/products ─────────────────────────────────────
	
	@Nested
	@DisplayName("GET /api/v1/products — get all products tests")
	class GetAllProductsTest {

		@Test
		@DisplayName("Should return all products")
		void shouldReturnAllProducts() throws Exception {
			ProductSummary p1 = ProductSummary.builder()
					.id(1L)
					.name("boAt Rockerz 450")
					.slug("boat-rockerz-450")
					.active(true).categoryId(4L)
					.categoryName("Headphones")
					.build();

			ProductSummary p2 = ProductSummary.builder()
					.id(2L)
					.name("DELL Inspiron")
					.slug("dell-inspiron")
					.active(true)
					.categoryId(4L)
					.categoryName("Laptops")
					.build();

			when(productService.getAllProducts()).thenReturn(List.of(p1, p2));

			mockMvc.perform(
					get("/api/v1/products")
				)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(jsonPath("$[0].name").value("boAt Rockerz 450"))
			.andExpect(jsonPath("$[1].name").value("DELL Inspiron"));
		}

		@Test
		@DisplayName("Should return empty list when no products")
		void shouldReturnEmptyListWhenNoProducts() throws Exception {
			when(productService.getAllProducts()).thenReturn(List.of());

			mockMvc.perform(
					get("/api/v1/products")
				)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(0)));
		}
	}
	
	// ─── GET /api/v1/products/category/{categoryId} ─────────────────────────────────────
	
    @Nested
    @DisplayName("GET /api/v1/products/category/{id} — get by category tests")
    class GetProductsByCategoryTest {
    	
    	@Test
    	@DisplayName("Should return products by category id")
    	void shouldReturnProductsByCategoryId() throws Exception {
    		ProductSummary headphone = ProductSummary.builder()
    				.id(1L)
    				.name("boAt Rockerz 450")
    				.slug("boat-rockerz-450")
    				.active(true)
    				.categoryId(4L)
    				.categoryName("Electronics")
    				.build();
    		
    		ProductSummary laptop = ProductSummary.builder()
    				.id(2L)
    				.name("DELL-Inspiron")
    				.slug("dell-inspiron")
    				.active(true)
    				.categoryId(4L)
    				.categoryName("Electronics")
    				.build();
    		
    		when(productService.getProductsByCategory(4L)).thenReturn(List.of(headphone, laptop));
    		
    		mockMvc.perform(
    				get("/api/v1/products/category/4")
    			)
    		.andExpect(status().isOk())
    		.andExpect(jsonPath("$", hasSize(2)))
    		.andExpect(jsonPath("$[0].name").value("boAt Rockerz 450"))
    		.andExpect(jsonPath("$[1].name").value("DELL-Inspiron"))
    		.andExpect(jsonPath("$[0].categoryId").value(4L))
    		.andExpect(jsonPath("$[1].categoryId").value(4L));
    		
    		verify(productService, times(1)).getProductsByCategory(4L);
    		verifyNoMoreInteractions(productService);
    	}
    	
    	@Test
    	@DisplayName("Should return empty list when category has no product")
    	void shouldReturnEmptyListWhenCategoryHasNoProduct() throws Exception {
    		when(productService.getProductsByCategory(2L)).thenReturn(List.of());
    		
    		mockMvc.perform(
    				get("/api/v1/products/category/2")
    			)
    		.andExpect(status().isOk())
    		.andExpect(jsonPath("$", hasSize(0)));
    	}
    	
    	@Test
    	@DisplayName("Should return 404 when category category not found")
    	void shouldReturn404WhenCategoryNotFound() throws Exception {
    		when(productService.getProductsByCategory(999L)).thenThrow(new ResourceNotFoundException("Category not found"));
    		
    		mockMvc.perform(
    				get("/api/v1/products/category/999")
    			)
    		.andExpect(status().isNotFound())
    		.andExpect(jsonPath("$.status").value(404))
    		.andExpect(jsonPath("$.message").value("Category not found"));
    	}
    }
	
	// ─── PUT /api/v1/products/{id} ─────────────────────────────────────
	
    @Nested
    @DisplayName("PUT /api/v1/products/{id} — update product tests")
    class UpdateProductTest {
    	
    	@Test
    	@DisplayName("Should update product and return 200")
    	void shouldUpdateProductAndReturn200() throws JsonProcessingException, Exception {
    		ProductRequest updatedRequest = new ProductRequest("boAt Rockerz 550", "Bluetooth headphone", false, 4L);

    	    ProductResponse updatedResponse = ProductResponse.builder()
    	            .id(1L)
    	            .name("boAt Rockerz 550")
    	            .slug("boat-rockerz-550")
    	            .active(false)
    	            .categoryId(4L)
    	            .categoryName("Headphones")
    	            .categorySlug("headphones")
    	            .build();
    		
    		when(productService.updateProduct(eq(1L), any(ProductRequest.class))).thenReturn(updatedResponse);
    		
    		mockMvc.perform(
    				put("/api/v1/products/1")
    				.contentType(MediaType.APPLICATION_JSON)
    				.content(objectMapper.writeValueAsString(updatedRequest))
    			)
    		.andExpect(status().isOk())
    	    .andExpect(jsonPath("$.name").value("boAt Rockerz 550"))
    	    .andExpect(jsonPath("$.slug").value("boat-rockerz-550"))
    		.andExpect(jsonPath("$.active").value(false));
    		
    		verify(productService, times(1)).updateProduct(eq(1L), any(ProductRequest.class));
    		verifyNoMoreInteractions(productService);
    	}
    	
    	@Test
    	@DisplayName("Should return 404 when updating non existing product")
    	void shouldReturn404WhenUpdatingNonExistingProduct() throws JsonProcessingException, Exception {
    		when(productService.updateProduct(eq(999L), any(ProductRequest.class))).thenThrow(new ResourceNotFoundException("Product not found"));
    		
    		mockMvc.perform(
    				put("/api/v1/products/999")
    				.contentType(MediaType.APPLICATION_JSON)
    				.content(objectMapper.writeValueAsString(productRequest))
    			)
    		.andExpect(status().isNotFound())
    		.andExpect(jsonPath("$.status").value(404))
    		.andExpect(jsonPath("$.message").value("Product not found"));
    	}
    	
    	@Test
    	@DisplayName("Should return 404 when updating product with non existing category")
    	void shouldReturn404WhenUpdatingProductWithNonExistingCategory() throws JsonProcessingException, Exception {
    		productRequest.setCategoryId(999L);
    		
    		when(productService.updateProduct(eq(1L), any(ProductRequest.class))).thenThrow(new ResourceNotFoundException("Category not found"));
    		
    		mockMvc.perform(
    				put("/api/v1/products/1")
    				.contentType(MediaType.APPLICATION_JSON)
    				.content(objectMapper.writeValueAsString(productRequest))
    			)
    		.andExpect(status().isNotFound())
    		.andExpect(jsonPath("$.status").value(404))
    		.andExpect(jsonPath("$.message").value("Category not found"));
    	}
    }
}

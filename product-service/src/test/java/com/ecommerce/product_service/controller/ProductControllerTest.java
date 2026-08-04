package com.ecommerce.product_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ecommerce.product_service.dto.ProductDto.ProductRequest;
import com.ecommerce.product_service.dto.ProductDto.ProductResponse;
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
	
	private ProductRequest productRequest;
	private ProductResponse productResponse;
	
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

	// ─── GET /api/v1/products/{id} ─────────────────────────────────────
	
	@Test
	@DisplayName("Should return product when find by id")
	void shouldReturnProductWhenFindById() throws Exception {
		when(productService.getProductById(1L)).thenReturn(productResponse);
		
		mockMvc.perform(
				get("/api/v1/products/1")
				.contentType(MediaType.APPLICATION_JSON)
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
				.contentType(MediaType.APPLICATION_JSON)
			)
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.status").value(404))
		.andExpect(jsonPath("$.message").value("Product not found"));
	}
	
	// ─── GET /api/v1/products/slug/{slug} ─────────────────────────────────────
	
	@Test
	@DisplayName("Should return product when find by slug")
	void shouldReturnProductWhenFindBySlug() throws Exception {
		when(productService.getProductBySlug("boat-rockerz-450")).thenReturn(productResponse);
		
		mockMvc.perform(
				get("/api/v1/products/slug/boat-rockerz-450")
				.contentType(MediaType.APPLICATION_JSON)
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
				.contentType(MediaType.APPLICATION_JSON)
			)
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.status").value(404))
		.andExpect(jsonPath("$.message").value("Product not found"));
	}
}

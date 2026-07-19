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

import com.ecommerce.product_service.dto.CategoryDto.CategoryRequest;
import com.ecommerce.product_service.dto.CategoryDto.CategoryResponse;
import com.ecommerce.product_service.exception.ResourceNotFoundException;
import com.ecommerce.product_service.security.JwtAuthenticationFilter;
import com.ecommerce.product_service.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = CategoryController.class)
@DisplayName("CategoryController Tests")
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTest {

	@Autowired
	private MockMvc mockMvc; // Perform HTTP requests
	
	@MockitoBean
	JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockitoBean // mock service
	private CategoryService categoryService;

	@Autowired
	private ObjectMapper objectMapper;
	
	private CategoryResponse response;
	
	@BeforeEach
	void setUp() {
		response = CategoryResponse.builder()
				.id(1L)
				.name("Electronics")
				.description("This is root category for all electronic items")
				.slug("electronics")
				.parentId(null)
				.parentCategoryName(null)
				.parentSlug(null)
				.build();
	}

	// ─── POST /api/v1/categories ─────────────────────────────────────
	
	@Test
	@DisplayName("Should create category and return 201")
	void shouldCreateCategoryAndReturn201() throws Exception {
		CategoryRequest categoryRequest = new CategoryRequest("Electronics", "This is root category for all electronic items", null);
		
		when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(response);
		
		mockMvc.perform(
				post("/api/v1/categories")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(categoryRequest))
			)
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.name").value("Electronics"))
			.andExpect(jsonPath("$.slug").value("electronics"))
			.andExpect(jsonPath("$.parentId").doesNotExist());
		
		verify(categoryService, times(1)).createCategory(any(CategoryRequest.class));
	}
	
	@Test
	@DisplayName("Should return 400 when name is blank")
	void shouldReturn400WhenNameisBlank() throws Exception {
		CategoryRequest categoryRequest = new CategoryRequest("", "This is root category for all electronic items", null);
		
		mockMvc.perform(
				post("/api/v1/categories")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(categoryRequest))
			)
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.status").value(400))
		.andExpect(jsonPath("$.message").exists());
		
		verify(categoryService, never()).createCategory(any());
	}
	
	// ─── GET /api/v1/categories/{id} ─────────────────────────────────────
	
	@Test
	@DisplayName("Should return category when find by id")
	void shouldReturnCategoryWhenFindById() throws Exception {
		when(categoryService.getCategoryById(1L)).thenReturn(response);
		
		mockMvc.perform(
				get("/api/v1/categories/1")
				.contentType(MediaType.APPLICATION_JSON)
			)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(1L))
		.andExpect(jsonPath("$.name").value("Electronics"))
		.andExpect(jsonPath("$.slug").value("electronics"))
		.andExpect(jsonPath("$.parentId").doesNotExist());
		
		verify(categoryService, times(1)).getCategoryById(1L);
	}
	
	@Test
	@DisplayName("Should return 404 when category not found by id")
	void shouldReturn404WhenCategoryNotFoundById() throws Exception {
		when(categoryService.getCategoryById(999L)).thenThrow(new ResourceNotFoundException("Category not found"));
		
		mockMvc.perform(
				get("/api/v1/categories/999")
				.contentType(MediaType.APPLICATION_JSON)
			)
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.status").value(404))
		.andExpect(jsonPath("$.message").value("Category not found"));
		
		verify(categoryService, times(1)).getCategoryById(999L);
	}
	
}

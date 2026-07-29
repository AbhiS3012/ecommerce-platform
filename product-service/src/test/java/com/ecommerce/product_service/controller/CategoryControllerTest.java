package com.ecommerce.product_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ecommerce.product_service.dto.CategoryDto.CategoryRequest;
import com.ecommerce.product_service.dto.CategoryDto.CategoryResponse;
import com.ecommerce.product_service.dto.CategoryDto.CategorySummary;
import com.ecommerce.product_service.exception.ResourceNotFoundException;
import com.ecommerce.product_service.security.JwtAuthenticationFilter;
import com.ecommerce.product_service.service.CategoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = CategoryController.class)
@DisplayName("CategoryController Tests")
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTest {

	@Autowired
	private MockMvc mockMvc; // Perform HTTP requests
	
	@MockitoBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockitoBean // mock service
	private CategoryService categoryService;

	@Autowired
	private ObjectMapper objectMapper;
	
	private CategoryRequest categoryRequest;
	private CategoryResponse categoryResponse;
	
	@BeforeEach
	void setUp() {
		categoryRequest = new CategoryRequest("Electronics", "This is root category for all electronic items", null);
		
		categoryResponse = CategoryResponse.builder()
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
		when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(categoryResponse);
		
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
		when(categoryService.getCategoryById(1L)).thenReturn(categoryResponse);
		
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
	}
	
	// ─── GET /api/v1/categories/slug/{slug} ──────────────────────────
	
	@Test
	@DisplayName("Should return category when find by slug")
	void shouldReturnCategoryWhenFindBySlug() throws Exception {
		when(categoryService.getCategoryBySlug("electronics")).thenReturn(categoryResponse);
		
		mockMvc.perform(
				get("/api/v1/categories/slug/electronics")
				.contentType(MediaType.APPLICATION_JSON)
			)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(1L))
		.andExpect(jsonPath("$.name").value("Electronics"))
		.andExpect(jsonPath("$.parentId").doesNotExist());
		
		verify(categoryService, times(1)).getCategoryBySlug("electronics");
	}
	
	@Test
	@DisplayName("Should return 404 when category not found by slug")
	void shouldReturn404WhenCategoryNotFoundBySlug() throws Exception {
		when(categoryService.getCategoryBySlug("electronics")).thenThrow(new ResourceNotFoundException("Category not found with slug: electronics"));
		
		mockMvc.perform(
				get("/api/v1/categories/slug/electronics")
				.contentType(MediaType.APPLICATION_JSON)
			)
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.status").value(404))
		.andExpect(jsonPath("$.message").value("Category not found with slug: electronics"));
	}
	
	// ─── GET /api/v1/categories/roots ────────────────────────────────
	
	@Test
	@DisplayName("Should return all root categories")
	void shouldReturnAllRootCategories() throws Exception {
		CategorySummary electronics = CategorySummary.builder()
				.id(1L)
				.name("Electronics")
				.description("This is root category for all electronic items")
				.slug("electronics")
				.build();
		
		CategorySummary clothing = CategorySummary.builder()
				.id(2L)
				.name("Clothing")
				.description("This is root category for all clothing items")
				.slug("clothing")
				.build();
		
		when(categoryService.getRootCategories()).thenReturn(List.of(electronics, clothing));
		
		mockMvc.perform(
				get("/api/v1/categories/roots")
				.contentType(MediaType.APPLICATION_JSON)
			)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.length()").value(2))
		.andExpect(jsonPath("$[0].name").value("Electronics"))
		.andExpect(jsonPath("$[1].name").value("Clothing"));
		
		verify(categoryService, times(1)).getRootCategories();
	}
	
	@Test
	@DisplayName("Should return empty list when no root categories found")
	void shouldReturnEmptyListWhenNoRootCatgoriesFound() throws Exception {
		when(categoryService.getRootCategories()).thenReturn(List.of());
		
		mockMvc.perform(
				get("/api/v1/categories/roots")
				.contentType(MediaType.APPLICATION_JSON)
			)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.length()").value(0));
	}
	
	// ─── GET /api/v1/categories/{parentId}/subcategories ────────────────────────────────
	
	@Test
	@DisplayName("Should return all sub-categories of parent category")
	void shouldReturnAllSubCategoriesOfParentCategory() throws Exception {
		CategorySummary mobilePhones = CategorySummary.builder()
		        .id(2L)
		        .name("Mobile Phones")
		        .description("All types of smartphones and mobile phones")
		        .slug("mobile-phones")
		        .build();

		CategorySummary laptops = CategorySummary.builder()
		        .id(3L)
		        .name("Laptops")
		        .description("Laptops, notebooks, and ultrabooks")
		        .slug("laptops")
		        .build();
		
		when(categoryService.getSubCategories(1L)).thenReturn(List.of(mobilePhones, laptops));
		
		mockMvc.perform(
				get("/api/v1/categories/1/subcategories")
				.contentType(MediaType.APPLICATION_JSON)
			)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.length()").value(2))
		.andExpect(jsonPath("$[0].name").value("Mobile Phones"))
		.andExpect(jsonPath("$[1].name").value("Laptops"));
		
		verify(categoryService, times(1)).getSubCategories(1L);
	}
	
	@Test
	@DisplayName("Should return empty list when parent category has no sub-categories")
	void shouldReturnEmptyListWhenParentCategoryHasNoSubCategories() throws Exception {
		when(categoryService.getSubCategories(1L)).thenReturn(List.of());
		
		mockMvc.perform(
				get("/api/v1/categories/1/subcategories")
				.contentType(MediaType.APPLICATION_JSON)
			)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.length()").value(0));
	}
	
	// ─── PUT /api/v1/categories/{id} ────────────────────────────────
	
	@Test
	@DisplayName("Should update category and return 200")
	void shouldUpdateCategoryAndReturn200() throws JsonProcessingException, Exception {
		when(categoryService.updateCategory(eq(1L), any(CategoryRequest.class))).thenReturn(categoryResponse);
		
		mockMvc.perform(
				put("/api/v1/categories/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(categoryRequest))
			)
		.andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.name").value("Electronics"));
		
		verify(categoryService, times(1)).updateCategory(eq(1L), any(CategoryRequest.class));
	}
	
	@Test
	@DisplayName("Should return 404 when updating non existing entry")
	void shouldReturn404WhenUpdatingNonExistingEntry() throws JsonProcessingException, Exception {
		when(categoryService.updateCategory(eq(999L), any(CategoryRequest.class))).thenThrow(new ResourceNotFoundException("Category not found"));
		
		mockMvc.perform(
				put("/api/v1/categories/999")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(categoryRequest))
			)
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404));
	}
	
}

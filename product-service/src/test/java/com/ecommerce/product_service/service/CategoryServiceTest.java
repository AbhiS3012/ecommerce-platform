package com.ecommerce.product_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

import com.ecommerce.product_service.dto.CategoryDto.CategoryRequest;
import com.ecommerce.product_service.dto.CategoryDto.CategoryResponse;
import com.ecommerce.product_service.entity.Category;
import com.ecommerce.product_service.exception.InvalidOperationException;
import com.ecommerce.product_service.exception.ResourceNotFoundException;
import com.ecommerce.product_service.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class) // tells JUnit to use mockito
@DisplayName("CategoryService Tests")
public class CategoryServiceTest {

	@Mock // create a fake object
	private CategoryRepository categoryRepository;
	
    @Mock
    private SlugService slugService;

	@InjectMocks // creates real object and injects all @Mock objects into it
	private CategoryService categoryService;
	
    private Category category;
    private Category parentCategory;
    private CategoryRequest request;
    
	@BeforeEach
	void setup() {
		parentCategory = Category.builder()
                .name("Electronics")
                .slug("electronics")
                .description("This is root category for all electronic items")
                .build();
        ReflectionTestUtils.setField(parentCategory, "id", 1L);
        
        category = Category.builder()
                .name("Mobile Phones")
                .slug("mobile-phones")
                .description("All mobile phones")
                .parent(parentCategory)
                .build();
        ReflectionTestUtils.setField(category, "id", 2L);
        
        request = new CategoryRequest(
				"Mobile Phones", 
				"All mobile phones",
				1L);
	}
	
	@Test
	@DisplayName("Should create root category when parentId is null")
	void shouldCreateRootCategoryWhenParentIdIsNull() {
		// ARRANGE — set up mock behaviour
		CategoryRequest request = new CategoryRequest(
				"Electronics", 
				"This is root category for all electronic items",
				null);
		
		when(slugService.generateUniqueSlug(eq("Electronics"), any())).thenReturn("electronics");
		when(categoryRepository.save(any(Category.class))).thenReturn(parentCategory);
		
		// ACT — call the real method
		CategoryResponse response = categoryService.createCategory(request);
		
		// ASSERT — verify the result
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(1L);
		assertThat(response.getName()).isEqualTo("Electronics");
		assertThat(response.getSlug()).isEqualTo("electronics");
		assertThat(response.getDescription()).isEqualTo("This is root category for all electronic items");
		assertThat(response.getParentId()).isNull();
		assertThat(response.getParentCategoryName()).isNull();
		assertThat(response.getParentSlug()).isNull();
		
		// verify findById never called — no parent to fetch
		verify(categoryRepository, never()).findById(anyLong());
		verify(categoryRepository, times(1)).save(any(Category.class));
	}
	
	@Test
	@DisplayName("Should create category successfully")
	void shouldCreateCategorySuccessfully() {
		when(slugService.generateUniqueSlug(eq("Mobile Phones"), any())).thenReturn("mobile-phones");
		when(categoryRepository.save(any(Category.class))).thenReturn(category);
		when(categoryRepository.findById(request.getParentId())).thenReturn(Optional.of(parentCategory));
		
		CategoryResponse response = categoryService.createCategory(request);
		
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(2L);
		assertThat(response.getName()).isEqualTo("Mobile Phones");
		assertThat(response.getSlug()).isEqualTo("mobile-phones");
		assertThat(response.getDescription()).isEqualTo("All mobile phones");
		assertThat(response.getParentId()).isEqualTo(1L);
		assertThat(response.getParentCategoryName()).isEqualTo("Electronics");
		assertThat(response.getParentSlug()).isEqualTo("electronics");
		
		verify(categoryRepository, times(1)).findById(anyLong());
		verify(categoryRepository, times(1)).save(any(Category.class));
	}
	
	@Test
	@DisplayName("Should throw ResourceNotFoundException when parent not found")
	void shouldThrowExceptionWhenParentNotFound() {

		when(slugService.generateUniqueSlug(eq("Mobile Phones"), any())).thenReturn("mobile-phones");
		when(categoryRepository.findById(eq(1L))).thenReturn(Optional.empty());

		// verify exception is thrown
		ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
				() -> categoryService.createCategory(request));

		assertThat(ex.getMessage()).isEqualTo("Parent category not found");

		// verify save never called
		verify(categoryRepository, never()).save(any());
	}

	@Test
	@DisplayName("Should return category when found by id")
	void shouldReturnCategoryWhenFoundById() {

		when(categoryRepository.findByIdWithParent(2L)).thenReturn(Optional.of(category));

		CategoryResponse response = categoryService.getCategoryById(2L);

		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(2L);
		assertThat(response.getName()).isEqualTo("Mobile Phones");
		assertThat(response.getParentId()).isEqualTo(1L);
	}

	@Test
	@DisplayName("Should throw ResourceNotFoundException when category not found by id")
	void shouldThrowExceptionWhenCategoryNotFoundById() {

		when(categoryRepository.findByIdWithParent(999L)).thenReturn(Optional.empty());

		ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
				() -> categoryService.getCategoryById(999L));

		assertThat(ex.getMessage()).isEqualTo("Category not found");
	}

    @Test
    @DisplayName("Should update category name and regenerate slug")
    void shouldUpdateCategoryNameAndRegenerateSlug() {
    	CategoryRequest updateRequest = new CategoryRequest("Smart Phones", "All smartphones", 1L);

        Category updatedCategory = Category.builder()
                .name("Smart Phones")
                .slug("smart-phones")
                .description("All smartphones")
                .parent(parentCategory)
                .build();
        ReflectionTestUtils.setField(updatedCategory, "id", 2L);

        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
		when(slugService.generateUniqueSlug(eq("Smart Phones"), any())).thenReturn("smart-phones");
		when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        CategoryResponse response = categoryService.updateCategory(2L, updateRequest);

        assertThat(response.getName()).isEqualTo("Smart Phones");
        assertThat(response.getSlug()).isEqualTo("smart-phones");
    }

	@Test
	@DisplayName("Should throw InvalidOperationException when category is its own parent")
	void shouldThrowExceptionWhenCategoryIsOwnParent() {
		CategoryRequest selfParentRequest = new CategoryRequest("Mobile Phones", "All mobile phones", 2L);

		when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));

		InvalidOperationException ex = assertThrows(InvalidOperationException.class,
				() -> categoryService.updateCategory(2L, selfParentRequest));

		assertThat(ex.getMessage()).isEqualTo("Category cannot be its own parent");

		verify(categoryRepository, never()).save(any());
	}

}

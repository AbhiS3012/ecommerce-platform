package com.ecommerce.product_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
	
	@Test
	@DisplayName("Should create root category when parentId is null")
	void shouldCreateRootCategoryWhenParentIdIsNull() {
		//Arrange
		CategoryRequest categoryRequest = new CategoryRequest(
				"Electronics", 
				"This is root category for all electronic items", 
				null);
		
		Category category = Category.builder()
				.name("Electronics")
				.slug("electronics")
				.description("This is root category for all electronic items")
				.parent(null)
				.build();
		ReflectionTestUtils.setField(category, "id", 1L);
		
		when(slugService.generateUniqueSlug(eq("Electronics"), any())).thenReturn("electronics");
		
		when(categoryRepository.save(any(Category.class))).thenReturn(category);
		
		//Act
		CategoryResponse response = categoryService.createCategory(categoryRequest);
		
		//Assert
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
}

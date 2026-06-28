package com.ecommerce.product_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SlugService Tests")
public class SlugServiceTest {
	
	private SlugService slugService;
	
	@BeforeEach
	void setUp() {
		slugService = new SlugService();
	}
	
	@Test
	@DisplayName("Should generate slug from simple name")
	void generateSlugFromSimpleName() {
		assertEquals("electronics", slugService.generateSlug("Electronics"));
	}

	@Test
	@DisplayName("Should convert uppercase to lowercase")
	void shouldConvertupperCaseToLowerCase() {
		assertEquals("laptops", slugService.generateSlug("LAPTOPS"));
	}

	@Test
	@DisplayName("Should replace spaces with hyphnes")
	void shouldReplaceSpacesWithHyphnes() {
		assertEquals("mobile-phones", slugService.generateSlug("Mobile Phones"));
	}

	@Test
	@DisplayName("Should remove special characters")
	void shouldRemoveSpecialCharacters() {
		assertEquals("mens-clothing", slugService.generateSlug("Men's Clothing"));
	}

	@Test
	@DisplayName("Should handle multiple spaces")
	void shouldHandleMultipleSpaces() {
		assertEquals("mobile-phones", slugService.generateSlug("Mobile   Phones"));
	}
	
	@Test
	@DisplayName("Should generate unique slug when no conflict")
	void shouldGenereteUniqueSlugWhenNoConflict() {
		String slug = slugService.generateUniqueSlug("Electronics", s -> List.of());
		assertThat(slug).isEqualTo("electronics");
	}

	@Test
	@DisplayName("Should append counter when slug exists")
	void shouldAppendCounterWhenSlugExists() {
		String slug = slugService.generateUniqueSlug("Electronics", s -> List.of("electronics"));
		assertThat(slug).isEqualTo("electronics-1");
	}

	@Test
	@DisplayName("Should find next available slug")
	void shouldFindNextAvailableSlug() {
		String result = slugService.generateUniqueSlug("Electronics",
				s -> List.of("electronics", "electronics-1", "electronics-2"));
		assertThat(result).isEqualTo("electronics-3");
	}
	
}

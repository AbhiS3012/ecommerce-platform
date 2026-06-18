package com.ecommerce.product_service.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SlugUtilTest {

	@Test
	@DisplayName("Should generate slug from simple name")
	void generateSlugFromSimpleName() {
		assertEquals("electronics", SlugUtil.generateSlug("Electronics"));
	}

	@Test
	@DisplayName("Should convert uppercase to lowercase")
	void shouldConvertupperCaseToLowerCase() {
		assertEquals("laptops", SlugUtil.generateSlug("LAPTOPS"));
	}

	@Test
	@DisplayName("Should replace spaces with hyphnes")
	void shouldReplaceSpacesWithHyphnes() {
		assertEquals("mobile-phones", SlugUtil.generateSlug("Mobile Phones"));
	}

	@Test
	@DisplayName("Should remove special characters")
	void shouldRemoveSpecialCharacters() {
		assertEquals("mens-clothing", SlugUtil.generateSlug("Men's Clothing"));
	}

	@Test
	@DisplayName("Should handle multiple spaces")
	void shouldHandleMultipleSpaces() {
		assertEquals("mobile-phones", SlugUtil.generateSlug("Mobile   Phones"));
	}
}

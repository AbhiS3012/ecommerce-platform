package com.ecommerce.product_service.util;

public class SlugUtil {
	
	public static String generateSlug(String name) {
		return name.toLowerCase()
				.trim()
				.replaceAll("[^a-z0-9\\s-]", "")
				.replaceAll("\\s+", "-");
	}
	
}

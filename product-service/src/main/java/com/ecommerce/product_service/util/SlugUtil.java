package com.ecommerce.product_service.util;

import java.util.List;
import java.util.function.Function;

public class SlugUtil {
	
	public static String generateSlug(String name) {
		return name.toLowerCase()
				.trim()
				.replaceAll("[^a-z0-9\\s-]", "")
				.replaceAll("\\s+", "-");
	}
	
	public static String generateUniqueSlug(String name, Function<String, List<String>> findSlugs) {
		String slug = generateSlug(name);

		List<String> existingSlugs = findSlugs.apply(slug);

		if (existingSlugs.isEmpty()) {
			return slug;
		}

		int counter = 1;
		String newSlug = slug + "_" + counter;

		while (existingSlugs.contains(newSlug)) {
			counter++;
			newSlug = slug + "_" + counter;
		}

		return newSlug;
	}
	
}

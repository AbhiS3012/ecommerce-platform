package com.ecommerce.product_service.service;

import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Service;

@Service
public class SlugService {
	
	public String generateSlug(String name) {
		return name.toLowerCase()
				.trim()
				.replaceAll("[^a-z0-9\\s-]", "")
				.replaceAll("\\s+", "-");
	}
	
	public String generateUniqueSlug(String name, Function<String, List<String>> findSlugs) {
		String slug = generateSlug(name);

		List<String> existingSlugs = findSlugs.apply(slug);

		if (existingSlugs.isEmpty()) {
			return slug;
		}

		int counter = 1;
		String newSlug = slug + "-" + counter;

		while (existingSlugs.contains(newSlug)) {
			counter++;
			newSlug = slug + "-" + counter;
		}

		return newSlug;
	}
	
}

package com.ecommerce.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ProductDto {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ProductRequest {
		@NotBlank(message = "Name is required")
		private String name;

		private String description;

		@Builder.Default
		private boolean active = true;

		@NotNull(message = "Category is required")
		private Long categoryId;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ProductResponse {
		private Long id;
		private String name;
		private String description;
		private String slug;
		private boolean active;
		private Long categoryId;
		private String categoryName;
		private String categorySlug;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ProductSummary {
		private Long id;
		private String name;
		private String slug;
		private boolean active;
		private Long categoryId;
		private String categoryName;
	}

}

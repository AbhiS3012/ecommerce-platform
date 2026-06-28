package com.ecommerce.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CategoryDto {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CategoryRequest {
		@NotBlank(message = "Name is required")
		private String name;

		private String description;

		private Long parentId;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class CategoryResponse {
		private Long id;
		private String name;
		private String slug;
		private String description;
		private Long parentId;
		private String parentSlug;
		private String parentCategoryName;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class CategorySummary {
		private Long id;
		private String name;
		private String slug;
		private String description;
	}

}

package com.ecommerce.product_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ecommerce.product_service.entity.Product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ProductDto {

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class CreateRequest {
		@NotBlank(message = "Name is required")
		private String name;

		private String description;

		@NotNull(message = "price is required")
		@DecimalMin(value = "0.0", message = "Price must be greater than 0", inclusive = false)
		private BigDecimal price;

		@NotNull(message = "Stock is required")
		@Min(value = 0, message = "Stock cannot be negative")
		private Integer stock;

		@NotNull(message = "Category is required")
		private Product.Category category;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class UpdateRequest {
		private String description;
		private BigDecimal price;
		private Integer stock;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ProductResponse {
		private Long id;
		private String name;
		private String description;
		private BigDecimal price;
		private Integer stock;
		private Product.Category category;
		private Product.Status status;
		private LocalDateTime createdAt;
	}

}

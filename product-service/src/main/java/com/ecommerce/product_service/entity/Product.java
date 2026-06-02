package com.ecommerce.product_service.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products", uniqueConstraints = {
		@UniqueConstraint(name = "uk_product_name_category", columnNames = { "name", "category" }) })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(length = 1000)
	private String description;

	@Column(nullable = false)
	private BigDecimal price;

	@Column(nullable = false)
	private Integer stock;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Category category;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status = Status.ACTIVE;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	public enum Category {
		ELECTRONICS, CLOTHING, BOOKS, FOOD, OTHER
	}
	
	public enum Status {
		ACTIVE, INACTIVE, DISCONTINUED
	}

}

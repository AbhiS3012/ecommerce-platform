package com.ecommerce.product_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "categories")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Category extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, unique = true)
	private String slug;

	@Column(columnDefinition = "TEXT")
	private String description;

	@ManyToOne
	@JoinColumn(name = "parent_id")
	private Category parent;

}

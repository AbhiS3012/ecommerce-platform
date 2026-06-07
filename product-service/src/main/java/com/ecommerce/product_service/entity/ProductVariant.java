package com.ecommerce.product_service.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProductVariant extends BaseEntity {

	@Column(nullable = false, unique = true)
	private String sku; // e.g. "APPLE-450-ALK-M" (Product Code)

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal price;

	@Column(nullable = false)
	@Builder.Default
	private int stockQuantity = 0;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	@OneToMany(mappedBy = "variant")
	@Builder.Default
	private List<VariantAttribute> variantAttributes = new ArrayList<>();

}

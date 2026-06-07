package com.ecommerce.product_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "variant_attributes", uniqueConstraints = @UniqueConstraint(columnNames = { "variant_id",
		"attribute_value_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class VariantAttribute extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "variant_id", nullable = false)
	private ProductVariant variant;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attribute_value_id", nullable = false)
	private AttributeValue attributeValue;

}

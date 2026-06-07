package com.ecommerce.product_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "attribute_values")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AttributeValue extends AuditableEntity {

	@Column(nullable = false)
	private String value; // e.g. "Red", "XL", "Cotton"

	@ManyToOne
	@JoinColumn(name = "attribute_id")
	private Attribute attribute;

}

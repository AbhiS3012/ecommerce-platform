package com.ecommerce.product_service.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "attributes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Attribute extends BaseEntity {

	@Column(nullable = false)
	private String name; // e.g. "Color", "Size"
	
	@Column(nullable = false)
	private String slug;

	@OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<AttributeValue> values = new ArrayList<>();

}

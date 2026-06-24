package com.ecommerce.product_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecommerce.product_service.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@EntityGraph(attributePaths = { "category" })
	@Query("SELECT p FROM Product p WHERE p.id = :id")
	Optional<Product> findByIdWithCategory(@Param("id") Long id);

	@EntityGraph(attributePaths = { "category" })
	@Query("SELECT p FROM Product p WHERE p.slug = :slug")
	Optional<Product> findBySlugWithCategory(@Param("slug") String slug);

	@EntityGraph(attributePaths = { "category" })
	@Query("SELECT p FROM Product p")
	List<Product> findAllWithCategory();

	List<Product> findByCategoryId(Long categoryId);
	
	@Query("SELECT p.slug FROM Product p WHERE p.slug LIKE :slug%")
	List<String> findSlugsStartsWith(@Param("slug") String slug);

}

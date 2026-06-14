package com.ecommerce.product_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecommerce.product_service.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	@EntityGraph(attributePaths = { "parent" })
	@Query("SELECT c FROM Category c WHERE c.id = :id")
	Optional<Category> findByIdWithParent(@Param("id") Long id);

	@EntityGraph(attributePaths = { "parent" })
	@Query("SELECT c FROM Category c WHERE c.slug = :slug")
	Optional<Category> findBySlugWithParent(@Param("slug") String slug);

	List<Category> findByParentIsNull();

	List<Category> findByParentId(Long parentId);

}

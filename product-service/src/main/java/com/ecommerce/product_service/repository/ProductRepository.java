package com.ecommerce.product_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.product_service.entity.Product;
import com.ecommerce.product_service.entity.Product.Category;
import com.ecommerce.product_service.entity.Product.Status;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	boolean existsByNameAndCategory(String name, Category category);

	List<Product> findByCategory(Category category);

	List<Product> findByStatus(Status status);

	Optional<Product> findByIdAndStatus(Long id, Status status);

	List<Product> findByCategoryAndStatus(Category category, Status status);

}

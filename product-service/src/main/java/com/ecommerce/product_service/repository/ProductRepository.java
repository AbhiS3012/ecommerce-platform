package com.ecommerce.product_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.product_service.entity.Product;
import com.ecommerce.product_service.entity.Product.Category;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findByActiveTrue();

	List<Product> findByCategoryAndActiveTrue(Product.Category category);

	boolean existsByNameAndCategory(String name, Category category);

	Product findByIdAndActiveTrue(Long id);

}

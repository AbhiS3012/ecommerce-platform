package com.ecommerce.product_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.product_service.dto.CategoryDto.CategoryRequest;
import com.ecommerce.product_service.dto.CategoryDto.CategoryResponse;
import com.ecommerce.product_service.dto.CategoryDto.CategorySummary;
import com.ecommerce.product_service.entity.Category;
import com.ecommerce.product_service.exception.InvalidOperationException;
import com.ecommerce.product_service.exception.ResourceNotFoundException;
import com.ecommerce.product_service.repository.CategoryRepository;
import com.ecommerce.product_service.util.SlugUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;
	
	@Transactional
	public CategoryResponse createCategory(CategoryRequest request) {
		//auto generate slug from name
		String slug = SlugUtil.generateSlug(request.getName());
		
		Category category = Category.builder()
				.name(request.getName())
				.description(request.getDescription())
				.slug(slug)
				.build();
		
		if(request.getParentId() != null) {
			Category parent = categoryRepository.findById(request.getParentId())
					.orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
			category.setParent(parent);
		}
		
		return toResponse(categoryRepository.save(category));
	}
	
	public CategoryResponse getCategoryById(Long id) {
		Category category = categoryRepository.findByIdWithParent(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found"));
		return toResponse(category);
	}

	public CategoryResponse getCategoryBySlug(String slug) {
		Category category = categoryRepository.findBySlugWithParent(slug)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));
		return toResponse(category);
	}
	
	public List<CategorySummary> getRootCategories() {
		return categoryRepository.findByParentIsNull().stream().map(this::toSummary).collect(Collectors.toList());
	}
	
	public List<CategorySummary> getSubCategories(Long parentId) {
		categoryRepository.findById(parentId).orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
		return categoryRepository.findByParentId(parentId).stream().map(this::toSummary).collect(Collectors.toList());
	}
	
	@Transactional
	public CategoryResponse updateCategory(Long id, CategoryRequest request) {
		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found"));
		
		if(!request.getName().equals(category.getName())) {
			String slug = SlugUtil.generateSlug(request.getName());
			category.setSlug(slug);
		}
		
		category.setName(request.getName());
		category.setDescription(request.getDescription());
		
		if(request.getParentId() != null) {
			
			if (request.getParentId().equals(id)) {
				throw new InvalidOperationException("Category cannot be its own parent");
			}
			
			Category parent = categoryRepository.findById(request.getParentId())
					.orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
			
			category.setParent(parent);
			
		} else {
			category.setParent(null);
		}
		
		return toResponse(categoryRepository.save(category));
	}

	//=============== Helper functions =================

	private CategoryResponse toResponse(Category category) {
		Category parent = category.getParent();
		
		return CategoryResponse.builder()
				.id(category.getId())
				.name(category.getName())
				.description(category.getDescription())
				.slug(category.getSlug())
				.parentId(parent != null ? parent.getId() : null)
				.parentSlug(parent != null ? parent.getSlug() : null)
				.parentCategoryName(parent != null ? parent.getName() : null)
				.build();
	}
	
	private CategorySummary toSummary(Category category) {
		return CategorySummary.builder()
				.id(category.getId())
				.name(category.getName())
				.description(category.getDescription())
				.slug(category.getSlug())
				.build();
	}
	
}

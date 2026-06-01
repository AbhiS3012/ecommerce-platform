package com.ecommerce.product_service.exception;

public class ProductAlredyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProductAlredyExistsException(String message) {
		super(message);
	}

}

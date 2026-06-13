package com.ecommerce.product_service.exception;

@SuppressWarnings("serial")
public class InvalidOperationException extends RuntimeException {

	public InvalidOperationException(String message) {
		super(message);
	}
}

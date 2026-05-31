package com.ecommerce.user_service.exception;

@SuppressWarnings("serial")
public class InvalidCaptchaException extends RuntimeException {

	public InvalidCaptchaException(String message) {
		super(message);
	}

}

package com.ecommerce.user_service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ecommerce.user_service.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(InvalidCaptchaException.class)
	public ResponseEntity<ApiResponse> handleInvalidCaptcha(InvalidCaptchaException ex) {
		logger.warn("Captcha error: {}", ex.getMessage());
		ApiResponse error = new ApiResponse("Captcha Error", ex.getMessage(), HttpStatus.FORBIDDEN.value());
		return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiResponse> handleBadCredentials(BadCredentialsException ex) {
		logger.warn("Bad credentials: {}", ex.getMessage());
		ApiResponse error = new ApiResponse("Authentication Failed", "Invalid username or password", HttpStatus.UNAUTHORIZED.value());
		return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
		logger.error("Unexpected error", ex);
		ApiResponse error = new ApiResponse("Server Error", "Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

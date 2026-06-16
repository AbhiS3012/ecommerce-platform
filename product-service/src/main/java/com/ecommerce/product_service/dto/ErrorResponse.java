package com.ecommerce.product_service.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

@Getter
public class ErrorResponse {

	private final int status;
	private final String message;
	
	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
	private final LocalDateTime timestamp;

	public ErrorResponse(int status, String message) {
		this.status = status;
		this.message = message;
		this.timestamp = LocalDateTime.now();
	}

}

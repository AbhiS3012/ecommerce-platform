package com.ecommerce.user_service.dto;

import lombok.Data;

@Data
public class ApiResponse {

	private final String error;

	private final String exceptionMsg;
	
	private final Integer status;

}

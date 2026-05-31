package com.ecommerce.user_service.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.user_service.dto.AuthenticationRequest;
import com.ecommerce.user_service.dto.RegisterRequest;
import com.ecommerce.user_service.security.JwtUtil;
import com.ecommerce.user_service.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
	
	@Value("${jwt.expiration-ms:}")
	private long expirationMs;
	
	private final AuthenticationManager authManager;
	private final JwtUtil jwtUtil;
	private final UserService service;
	
	@PostMapping("/login")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest request) {
		
		// Step 1: Proceed with authentication
		Authentication authentication = doAuthenticate(request.getEmail(), request.getPassword());
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		
		// Step 2: Proceed JWT generation
		final String token = jwtUtil.generateToken(userDetails);
		
		return ResponseEntity.ok(Map.of("token", token, "type", "Bearer"));
	}

	@PostMapping("/register")
	public ResponseEntity<?> signUp(@RequestBody RegisterRequest request) {
		service.save(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	private Authentication doAuthenticate(String userName, String password) {
		Authentication authentication = authManager
				.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return authentication;
	}
	
}

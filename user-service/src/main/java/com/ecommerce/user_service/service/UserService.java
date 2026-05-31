package com.ecommerce.user_service.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.user_service.dto.RegisterRequest;
import com.ecommerce.user_service.entity.User;
import com.ecommerce.user_service.entity.User.Role;
import com.ecommerce.user_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public void save(RegisterRequest request) {
		// Encode the raw password before saving
		String encodedPassword = passwordEncoder.encode(request.getPassword());

		User user = User.builder().email(request.getEmail()).firstName(request.getFirstName())
				.lastName(request.getLastName()).role(Role.ROLE_USER).password(encodedPassword).build();

		repository.save(user);
	}

}

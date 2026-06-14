package com.ecommerce.user_service.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	private static final String COOKIE_NAME = "access_token";

	private final CustomUserDetailService userDetailsService;
	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String jwt = extractToken(request);

		if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			try {
				String username = jwtUtil.extractUsername(jwt);

				if (username != null) {
					UserDetails userDetails = userDetailsService.loadUserByUsername(username);

					if (jwtUtil.validateToken(jwt, userDetails)) {

						UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities());

						usernamePasswordAuthenticationToken
								.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

						SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
					}
				}

			} catch (Exception e) {

			}
		}

		filterChain.doFilter(request, response);
	}

	public String extractToken(HttpServletRequest request) {

		final String authorizationHeader = request.getHeader("Authorization");

		// Check header
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			return authorizationHeader.substring(7);
		}
		
		// Check cookie
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (COOKIE_NAME.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}

		return null;
	}

}

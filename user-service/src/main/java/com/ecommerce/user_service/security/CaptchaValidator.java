package com.ecommerce.user_service.security;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class CaptchaValidator {

	@Value("${captcha.secret:}")
	private String captchaSecret;

	private static final String GOOGLE_RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

	@SuppressWarnings("rawtypes")
	public boolean validateCpatcha(String token) {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("secret", captchaSecret);
		params.add("response", token);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

		try {
			ResponseEntity<Map> response = restTemplate.postForEntity(GOOGLE_RECAPTCHA_VERIFY_URL, request, Map.class);
			Boolean success = (Boolean) response.getBody().get("success");
			return success != null && success;
		} catch (Exception e) {
			return false;
		}
	}
}

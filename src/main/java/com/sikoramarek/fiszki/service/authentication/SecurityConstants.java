package com.sikoramarek.fiszki.service.authentication;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

public class SecurityConstants {
	public static final String SECRET = "SecretKeyToGenJWTs";
	public static final long EXPIRATION_TIME = 864_000_000; // 10 days
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final String SIGN_UP_URL = "/users";

	public static boolean checkForAdmin() {
		Collection<? extends GrantedAuthority> authority = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		return authority.stream().anyMatch(o -> o.getAuthority().equals("ROLE_ADMIN"));
	}
}
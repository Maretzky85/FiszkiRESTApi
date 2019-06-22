package com.sikoramarek.fiszki.authentication;


import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sikoramarek.fiszki.model.UserModel;
import com.sikoramarek.fiszki.model.UserPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.sikoramarek.fiszki.authentication.SecurityConstants.*;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private AuthenticationManager authenticationManager;
	private UserDetailsServiceImpl userDetailsService;

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService) {
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
	                                            HttpServletResponse response) throws AuthenticationException {

		try {
			UserModel user = new ObjectMapper()
					.readValue(request.getInputStream(), UserModel.class);
			UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(user.getUsername());
			return authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							user.getUsername(),
							user.getPassword(),
							userPrincipal.getAuthorities())
			);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req,
	                                        HttpServletResponse res,
	                                        FilterChain chain,
	                                        Authentication auth) throws IOException, ServletException {

		String token = JWT.create()
				.withSubject(((User) auth.getPrincipal()).getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.sign(HMAC512(SECRET.getBytes()));
		res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
	}
}
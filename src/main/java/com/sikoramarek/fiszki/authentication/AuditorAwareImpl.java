package com.sikoramarek.fiszki.authentication;

import com.sikoramarek.fiszki.model.UserModel;
import com.sikoramarek.fiszki.model.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<UserModel> {


	@Autowired
	UserDetailsService userDetailsService;

	@Override
	public Optional getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			return null;
		}
		UserPrincipal userModel =
				(UserPrincipal) userDetailsService
						.loadUserByUsername(authentication.getPrincipal().toString());
		return Optional.of(userModel.getUser());
	}
}

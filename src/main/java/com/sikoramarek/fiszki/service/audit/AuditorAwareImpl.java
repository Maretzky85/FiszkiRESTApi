package com.sikoramarek.fiszki.service.audit;

import com.sikoramarek.fiszki.service.authentication.UserDetailsServiceImpl;
import com.sikoramarek.fiszki.model.UserModel;
import com.sikoramarek.fiszki.model.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<UserModel> {


	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Override
	public Optional getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			return Optional.empty();
		}

		return Optional.of(authentication.getPrincipal().toString());
	}
}

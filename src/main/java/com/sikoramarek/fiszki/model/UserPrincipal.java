package com.sikoramarek.fiszki.model;

import com.sikoramarek.fiszki.service.authentication.UserDetailsServiceImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UserPrincipal extends User {

	private final UserModel user;

	public UserPrincipal(UserModel user, Collection<? extends GrantedAuthority> authorities) {
		super(user.getUsername(), user.getPassword(), UserDetailsServiceImpl.getAuthorities(user.getRoles()));
		this.user = user;
	}

	public UserPrincipal(UserModel user, boolean enabled, boolean accountNonExpired,
	                     boolean credentialsNonExpired,
	                     boolean accountNonLocked,
	                     Collection<? extends GrantedAuthority> authorities) {
		super(user.getUsername(), user.getPassword(),
				enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, UserDetailsServiceImpl.getAuthorities(user.getRoles()));
		this.user = user;
	}

	public UserModel getUser() {
		return this.user;
	}
}
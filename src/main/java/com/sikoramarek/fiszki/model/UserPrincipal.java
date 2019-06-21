package com.sikoramarek.fiszki.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UserPrincipal extends User {

	private final UserModel user;

	public UserPrincipal(UserModel user, Collection<? extends GrantedAuthority> authorities) {
		super(user.getName(), user.getPassword(), AuthorityUtils.createAuthorityList("ROLE_"+user.getRole().role));
		this.user = user;
	}

	public UserPrincipal(UserModel user, boolean enabled, boolean accountNonExpired,
	                     boolean credentialsNonExpired,
	                     boolean accountNonLocked,
	                     Collection<? extends GrantedAuthority> authorities) {
		super(user.getName(), user.getPassword(),
				enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, AuthorityUtils.createAuthorityList("ROLE_"+user.getRole().role));
		this.user = user;
	}

	public UserModel getUser() {
		return this.user;
	}
}
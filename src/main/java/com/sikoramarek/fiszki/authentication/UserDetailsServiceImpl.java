package com.sikoramarek.fiszki.authentication;

import com.sikoramarek.fiszki.repository.UserRepository;
import com.sikoramarek.fiszki.model.Role;
import com.sikoramarek.fiszki.model.UserModel;
import com.sikoramarek.fiszki.model.UserPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private final UserRepository applicationUserRepository;

	public UserDetailsServiceImpl(UserRepository applicationUserRepository) {
		this.applicationUserRepository = applicationUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserModel applicationUser = applicationUserRepository.getUserByUsername(username);
		if (applicationUser == null) {
			throw new UsernameNotFoundException(username);
		}
		UserPrincipal principal = new UserPrincipal(
				applicationUser,
				true, true, true, true,
				getAuthorities(applicationUser.getRoles())
		);
		return principal;
	}

	public static List<GrantedAuthority> getAuthorities(Set<Role > roles) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getRole())));
		return authorities;
	}

}
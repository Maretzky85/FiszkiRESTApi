package com.sikoramarek.fiszki.authentication;

import com.sikoramarek.fiszki.model.DAO.UsersDAO;
import com.sikoramarek.fiszki.model.Roles;
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

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private final UsersDAO applicationUserRepository;

	public UserDetailsServiceImpl(UsersDAO applicationUserRepository) {
		this.applicationUserRepository = applicationUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserModel applicationUser = applicationUserRepository.getUserByName(username);
		if (applicationUser == null) {
			throw new UsernameNotFoundException(username);
		}
		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNotExpired = true;
		boolean accountNonLocked = true;
		UserPrincipal principal = new UserPrincipal(
				applicationUser,
				enabled, accountNonExpired, credentialsNotExpired, accountNonLocked,
				getAuthorities(applicationUser.getRole())
		);
		return principal;
	}

	private List<GrantedAuthority> getAuthorities(Roles role) {
		ArrayList<GrantedAuthority> roles = new ArrayList<>();
		roles.add(new SimpleGrantedAuthority("ROLE_"+role.getRole()));
		return roles;
	}

}
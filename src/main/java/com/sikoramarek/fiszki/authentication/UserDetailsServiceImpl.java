package com.sikoramarek.fiszki.authentication;

import com.sikoramarek.fiszki.model.DAO.UsersDAO;
import com.sikoramarek.fiszki.model.UserModel;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.util.Collections.emptyList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private UsersDAO applicationUserRepository;

	public UserDetailsServiceImpl(UsersDAO applicationUserRepository) {
		this.applicationUserRepository = applicationUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserModel applicationUser = applicationUserRepository.getUserByName(username);
		if (applicationUser == null) {
			throw new UsernameNotFoundException(username);
		}
		return new User(applicationUser.getName(), applicationUser.getPassword(), emptyList());
	}
}
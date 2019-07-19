package com.sikoramarek.fiszki.service;

import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.Role;
import com.sikoramarek.fiszki.model.UserModel;
import com.sikoramarek.fiszki.repository.QuestionRepository;
import com.sikoramarek.fiszki.repository.RoleRepository;
import com.sikoramarek.fiszki.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;

@Service
public class UserService {

	private QuestionService questionService;
	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	public UserService(UserRepository userRepository,
	                   RoleRepository roleRepository,
	                   BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.roleRepository = roleRepository;
	}

	public ResponseEntity<Collection> getAllUsers() {
		if (checkForAdmin()) {
			Collection<UserModel> users = userRepository.findAll();
			if (!users.isEmpty()) { users.forEach(userModel -> userModel.setPassword(null)); }
			return new ResponseEntity<>(users, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	}

	public ResponseEntity<UserModel> getUserById(Long id) {
		Optional<UserModel> user = userRepository.findById(id);
		if (user.isPresent()) {
			UserModel userToReturn = user.get();
			userToReturn.setPassword(null);
			return new ResponseEntity<>(userToReturn, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	public ResponseEntity<UserModel> saveNewUser(UserModel newUser) {
		if (newUser != null) {
			if (userRepository.existsUserModelByUsername(newUser.getUsername())){
				return new ResponseEntity<>(HttpStatus.CONFLICT);
			}
			try {
				newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
				Set<Role> roles = new HashSet<>();
				Role role = roleRepository.findRoleByRoleEquals("USER");
				roles.add(role);
				newUser.setRoles(roles);
				userRepository.save(newUser);
			} catch (DataAccessException e) {
				e.printStackTrace();
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<>(newUser, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	private boolean checkForAdmin() {
		Collection<? extends GrantedAuthority> authority = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		if (authority.stream().anyMatch(o -> o.getAuthority().equals("ROLE_ADMIN"))) {
			return true;
		} else {
			return false;
		}
	}
}

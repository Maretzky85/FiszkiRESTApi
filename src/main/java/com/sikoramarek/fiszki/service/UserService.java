package com.sikoramarek.fiszki.service;

import com.sikoramarek.fiszki.errors.BadRequestError;
import com.sikoramarek.fiszki.errors.ConflictError;
import com.sikoramarek.fiszki.errors.NoPermissionError;
import com.sikoramarek.fiszki.errors.NotFoundError;
import com.sikoramarek.fiszki.model.Role;
import com.sikoramarek.fiszki.model.UserModel;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.sikoramarek.fiszki.service.authentication.SecurityConstants.checkForAdmin;

@Service
public class UserService {

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

	public ResponseEntity<Collection<UserModel>> getAllUsers() {
		if (checkForAdmin()) {
			Collection<UserModel> users = userRepository.findAll();
			if (!users.isEmpty()) {
				users.forEach(userModel -> userModel.setPassword(null));
			}
			return new ResponseEntity<>(users, HttpStatus.OK);
		} else {
			throw new NoPermissionError("Only for Administrators");
		}
	}

	public ResponseEntity<UserModel> getUserById(Long userId) {
		Optional<UserModel> user = userRepository.findById(userId);
		if (user.isPresent()) {
			UserModel userToReturn = user.get();
			userToReturn.setPassword(null);
			return new ResponseEntity<>(userToReturn, HttpStatus.OK);
		} else {
			throw new NotFoundError("User of ID " + userId + " not found");
		}
	}

	public ResponseEntity<UserModel> saveNewUser(UserModel newUser) {
		if (newUser != null) {
			if (userRepository.existsUserModelByUsername(newUser.getUsername())) {
				throw new ConflictError(newUser.getUsername() + " exists");
			}
			try {
				newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
				Set<Role> roles = new HashSet<>();
				Role role = roleRepository.findRoleByRoleEquals("USER");
				roles.add(role);
				newUser.setRoles(roles);
				userRepository.save(newUser);
			} catch (DataAccessException e) {
				throw new BadRequestError(e.getMessage());
			}
			return new ResponseEntity<>(newUser, HttpStatus.CREATED);
		} else {
			throw new BadRequestError("Empty fileds not allowed");
		}
	}

	public ResponseEntity<UserModel> editUser(UserModel user){
		if (!checkForAdmin()) {
			throw new NoPermissionError("Only Admin can change");
		}
		if (!userRepository.existsUserModelByUsername(user.getUsername())) {
			throw new NotFoundError(user.getUsername() + "not found");
		}
		UserModel userToEdit = userRepository.getUserByUsername(user.getUsername());
		userToEdit.setRoles(user.getRoles());
		userRepository.save(userToEdit);
		return new ResponseEntity<>(userToEdit, HttpStatus.OK);
	}
}

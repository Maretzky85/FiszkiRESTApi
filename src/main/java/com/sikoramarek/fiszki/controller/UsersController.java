package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.model.UserModel;
import com.sikoramarek.fiszki.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UsersController {

	private UserService userService;

	@Autowired
	public UsersController(
			UserService userService) {
		this.userService = userService;
	}

	@GetMapping("users")
	public ResponseEntity<List<UserModel>> getAllUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("users/{user_id}")
	public ResponseEntity<UserModel> getUser(@PathVariable("user_id") long userId) {
		return userService.getUserById(userId);
	}

	@PostMapping("users")
	public ResponseEntity<UserModel> newUser(@Valid @RequestBody UserModel newUser) {
		return userService.saveNewUser(newUser);
	}


}

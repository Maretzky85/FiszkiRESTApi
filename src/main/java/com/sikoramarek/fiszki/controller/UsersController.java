package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.model.DAO.UsersDAO;
import com.sikoramarek.fiszki.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class UsersController extends ReturnController {

	private UsersDAO usersDAO;

	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public UsersController(
			@Autowired BCryptPasswordEncoder bCryptPasswordEncoder,
			@Autowired UsersDAO usersDAO){
		this.usersDAO = usersDAO;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@GetMapping("users")
	public ResponseEntity<List<UserModel>> getAllUsers(){
		List<UserModel> allUsers = usersDAO.findAll();
		if (!allUsers.isEmpty()){
			return new ResponseEntity<>(allUsers, HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("users/{user_id}")
	public ResponseEntity<UserModel> getUser(@PathVariable("user_id") long userId){
		Optional<UserModel> user = usersDAO.findById(userId);
		if (user.isPresent()){
			return new ResponseEntity<>(user.get(), HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("users")
	public ResponseEntity<UserModel> newUser(@RequestBody UserModel newUser){
		if (newUser != null){
			try {
				newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
				usersDAO.save(newUser);
			}catch (DataAccessException e){
				e.printStackTrace();
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
			return new ResponseEntity<>(newUser, HttpStatus.ACCEPTED);
		}else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

}

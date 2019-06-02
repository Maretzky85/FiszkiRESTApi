package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.model.DAO.AnswersDAO;
import com.sikoramarek.fiszki.model.DAO.QuestionsDAO;
import com.sikoramarek.fiszki.model.DAO.TagDAO;
import com.sikoramarek.fiszki.model.DAO.UsersDAO;
import com.sikoramarek.fiszki.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class FiszkiController extends ReturnController {

	private UsersDAO usersDAO;
	private QuestionsDAO questionsDAO;
	private AnswersDAO answersDAO;
	private TagDAO tagDAO;

	public FiszkiController(
			@Autowired UsersDAO usersDAO,
			@Autowired QuestionsDAO questionsDAO,
			@Autowired AnswersDAO answersDAO,
			@Autowired TagDAO tagDAO){
		this.usersDAO = usersDAO;
		this.questionsDAO = questionsDAO;
		this.answersDAO = answersDAO;
		this.tagDAO = tagDAO;
	}

	@GetMapping("users")
	public ResponseEntity<List<User>> getAllUsers(){
		List<User> allUsers = usersDAO.findAll();
		if (!allUsers.isEmpty()){
			return new ResponseEntity<>(allUsers, HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("users/{user_id}")
	public ResponseEntity<User> getUser(@PathVariable("user_id") long userId){
		Optional<User> user = usersDAO.findById(userId);
		if (user.isPresent()){
			return new ResponseEntity<>(user.get(), HttpStatus.OK);
		}else {
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("users")
	public ResponseEntity<User> newUser(@RequestBody User newUser){
		if (newUser != null){
			try {
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

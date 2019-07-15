package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.UserModel;
import com.sikoramarek.fiszki.service.QuestionService;
import com.sikoramarek.fiszki.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Collection;
import java.util.List;

@RestController
public class UsersController {

	private UserService userService;
	private QuestionService questionService;

	@Autowired
	public UsersController(
			UserService userService,
			QuestionService questionService) {
		this.userService = userService;
		this.questionService = questionService;
	}

	@PostMapping("users/mark_question/{question_id}")
	public ResponseEntity markQuestion(Principal principal, @PathVariable("question_id") Long question_id){
		return questionService.markQuestionAsKnown(principal, question_id);
	}

	@GetMapping("users/known_questions")
	public ResponseEntity<Collection<Question>> getKnownQuestions(Principal principal){
		if (principal == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		return questionService.getKnownQuestions(principal);
	}

	@PostMapping("users")
	public ResponseEntity<UserModel> newUser(@Valid @RequestBody UserModel newUser) {
		return userService.saveNewUser(newUser);
	}
}

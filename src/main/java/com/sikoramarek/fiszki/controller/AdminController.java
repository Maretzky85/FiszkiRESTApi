package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.repository.QuestionRepository;
import com.sikoramarek.fiszki.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class AdminController {

	private QuestionRepository questionRepository;
	private UserService userService;

	@Autowired
	public AdminController(QuestionRepository questionRepository, UserService userService){
		this.questionRepository = questionRepository;
		this.userService = userService;
	}

	@PostMapping("admin/accept/{questionId}")
	public ResponseEntity<Question> acceptQuestion(@PathVariable("questionId") Long questionId) {
		if (questionRepository.findQuestionById(questionId).isPresent()) {
			questionRepository.setAccepted(questionId);
			Question question = questionRepository.findQuestionById(questionId).get();
			question.setAccepted(true);
			return new ResponseEntity<>(question, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("admin/users")
	public ResponseEntity<Collection> getUsers() {
		return userService.getAllUsers();
	}
}

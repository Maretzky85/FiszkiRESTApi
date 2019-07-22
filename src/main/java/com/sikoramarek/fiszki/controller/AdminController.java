package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.UserModel;
import com.sikoramarek.fiszki.model.projections.AnswerOnly;
import com.sikoramarek.fiszki.model.projections.QuestionOnly;
import com.sikoramarek.fiszki.repository.QuestionRepository;
import com.sikoramarek.fiszki.service.AnswerService;
import com.sikoramarek.fiszki.service.QuestionService;
import com.sikoramarek.fiszki.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class AdminController {

	private QuestionRepository questionRepository;
	private QuestionService questionService;
	private AnswerService answerService;
	private UserService userService;

	@Autowired
	public AdminController(QuestionRepository questionRepository, QuestionService questionService, AnswerService answerService, UserService userService) {
		this.questionRepository = questionRepository;
		this.questionService = questionService;
		this.answerService = answerService;
		this.userService = userService;
	}

	@PostMapping("admin/accept/{questionId}")
	public ResponseEntity<Question> acceptQuestion(@PathVariable("questionId") Long questionId) {
		return questionService.acceptQuestion(questionId);
	}

	@GetMapping("admin/users")
	public ResponseEntity<Collection<UserModel>> getUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("admin/users/{userName}/questions")
	public ResponseEntity<Collection<QuestionOnly>> getUserQuestions(@PathVariable("userName") String userName) {
		return questionService.getUserQuestions(userName);
	}

	@GetMapping("admin/users/{userName}/answers")
	public ResponseEntity<Collection<AnswerOnly>> getUserAnswers(@PathVariable("userName") String userName) {
		return answerService.getUserAnswers(userName);
	}
}

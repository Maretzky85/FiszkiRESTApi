package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

	private QuestionRepository questionRepository;

	@Autowired
	public AdminController(QuestionRepository questionRepository){
		this.questionRepository = questionRepository;
	}

	@PostMapping("admin/accept/{questionId}")
	public ResponseEntity acceptQuestion(@PathVariable("questionId") Long questionId) {

		if (questionRepository.findQuestionById(questionId).isPresent()) {
			questionRepository.setAccepted(questionId);
			return new ResponseEntity(HttpStatus.OK);
		} else {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
	}

}

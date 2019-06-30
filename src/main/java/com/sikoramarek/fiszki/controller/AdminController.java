package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class AdminController {

	private QuestionRepository questionRepository;

	@Autowired
	public AdminController(QuestionRepository questionRepository){
		this.questionRepository = questionRepository;
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

}

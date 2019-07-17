package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.model.Answer;
import com.sikoramarek.fiszki.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class AnswerController{

	private AnswerService answerService;

	@Autowired
	public AnswerController(AnswerService answerService) {
		this.answerService = answerService;
	}


	@PutMapping("answers/{answerId}")
	public ResponseEntity<Answer> editAnswerById(@RequestBody Answer answer,
	                                             @PathVariable("answerId") Long answerId,
												 Principal principal) {
		return answerService.editAnswerById(answer, answerId, principal);
	}

	@DeleteMapping("answers/{answer_id}")
	public ResponseEntity<Answer> deleteAnswer(@PathVariable("answer_id") Long answerId,
											   Principal principal) {
		return answerService.deleteAnswer(answerId, principal);
	}
}

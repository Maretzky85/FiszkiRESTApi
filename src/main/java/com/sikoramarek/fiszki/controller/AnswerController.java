package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.model.Answer;
import com.sikoramarek.fiszki.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AnswerController{

	private AnswerService answerService;

	@Autowired
	public AnswerController(
			AnswerService answerService) {
		this.answerService = answerService;
	}

	@GetMapping("answers")
	public ResponseEntity<List<Answer>> getAllAnswers() {
		return answerService.getAllAnswers();
	}

	@GetMapping("answers/{answerId}")
	public ResponseEntity<List<Answer>> getAnswerById(@PathVariable("answerId") Long answerId) {
		return answerService.getAnswerById(answerId);
	}

	@PutMapping("answers/{answerId}")
	public ResponseEntity<Answer> editAnswerById(@RequestBody Answer answer,
	                                             @PathVariable("answerId") Long answerId) {
		return answerService.editAnswerById(answer, answerId);
	}

	@GetMapping("questions/{question_id}/answers")
	public ResponseEntity<List<Answer>> getAnswersByQuestionId(@PathVariable("question_id") Long questionId) {
		return answerService.getAnswersByQuestionId(questionId);
	}

	@PostMapping("questions/{question_id}/answers")
	public ResponseEntity<Answer> newAnswer(@RequestBody Answer answer, @PathVariable("question_id") Long question_id) {
		return answerService.newAnswer(answer, question_id);
	}

	@DeleteMapping("answers/{answer_id}")
	public ResponseEntity<Answer> deleteAnswer(@PathVariable("answer_id") Long answerId) {
		return answerService.deleteAnswer(answerId);
	}

}

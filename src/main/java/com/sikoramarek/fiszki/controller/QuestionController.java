package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.model.Answer;
import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.service.AnswerService;
import com.sikoramarek.fiszki.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class QuestionController{

	private QuestionService questionService;
	private AnswerService answerService;

	@Autowired
	public QuestionController(QuestionService questionService, AnswerService answerService) {
		this.questionService = questionService;
		this.answerService = answerService;
	}


	@GetMapping(value = "questions", params = {"page", "size"})
	public Page<Question> getPageableQuestions(@RequestParam("page") int page, @RequestParam("size") int size) {
		return questionService.getPageableQuestions(page, size);
	}

	@GetMapping("questions/{questionId}")
	public ResponseEntity<List<Question>> getQuestionById(@PathVariable("questionId") Long questionId) {
		return questionService.getQuestionById(questionId);
	}

	@PostMapping("questions")
	public ResponseEntity<Question> newQuestion(@RequestBody Question question) {
		return questionService.newQuestion(question);
	}

	@PutMapping("questions/{questionId}")
	public ResponseEntity<Question> editQuestion(@RequestBody Question question,
	                                             @PathVariable("questionId") Long questionId,
												 Principal principal) {
		return questionService.editQuestion(question, questionId, principal);
	}

	@DeleteMapping("questions/{questionId}")
	public ResponseEntity<Question> deleteQuestion(@PathVariable("questionId") Long questionId,
												   Principal principal) {
		return questionService.deleteQuestion(questionId, principal);
	}

	@GetMapping("questions/random")
	public ResponseEntity<List<Question>> getRandom(Principal principal) {
		return questionService.getRandom(principal);
	}

	@GetMapping("questions/{question_id}/answers")
	public ResponseEntity<List<Answer>> getAnswersByQuestionId(@PathVariable("question_id") Long questionId) {
		return answerService.getAnswersByQuestionId(questionId);
	}

	@PostMapping("questions/{question_id}/answers")
	public ResponseEntity<Answer> newAnswer(@RequestBody Answer answer,
											@PathVariable("question_id") Long question_id) {
		return answerService.newAnswer(answer, question_id);
	}
}

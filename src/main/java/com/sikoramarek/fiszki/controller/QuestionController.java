package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.model.Question;
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

	@Autowired
	public QuestionController(QuestionService questionService) {
		this.questionService = questionService;
	}

	@GetMapping("questions")
	public Page<Question> getAllQuestions() {
		return questionService.getAllQuestions();
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
	                                             @PathVariable("questionId") Long questionId) {
		return questionService.editQuestion(question, questionId);
	}

	@DeleteMapping("questions/{questionId}")
	public ResponseEntity<Question> deleteQuestion(@PathVariable("questionId") Long questionId) {
		return questionService.deleteQuestion(questionId);
	}

	@GetMapping("questions/random")
	public ResponseEntity<List<Question>> getRandom(Principal principal) {
		return questionService.getRandom(principal);
	}

	@GetMapping("questions/admin")
	public ResponseEntity<Page<Question>> getUnaccepted() {return questionService.getUnaccepted(0, 10); }
}

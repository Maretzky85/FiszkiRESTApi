package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.model.Answer;
import com.sikoramarek.fiszki.model.DAO.AnswersDAO;
import com.sikoramarek.fiszki.model.DAO.QuestionsDAO;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class AnswerController extends ReturnController {

	private AnswersDAO answersDAO;
	private QuestionsDAO questionsDAO;

	public AnswerController (
			@Autowired AnswersDAO answersDAO,
			@Autowired QuestionsDAO questionsDAO){

		this.questionsDAO = questionsDAO;
		this.answersDAO = answersDAO;
	}

	@GetMapping("answers")
	public ResponseEntity<List<Answer>> getAllAnswers() {
		return returnCollectionIfNotEmpty(answersDAO.findAll());
	}

	@GetMapping("answers/{answer_id}")
	public ResponseEntity<Answer> getAnswerById(@PathVariable("answer_id") Long answer_id){
		return returnIfNotEmpty(answersDAO.findById(answer_id));
	}

	@PutMapping("answers/{answer_id}")
	public ResponseEntity<Answer> editAnswerById(@RequestBody Answer answer,
	                                             @PathVariable("answer_id") Long answer_id){
			Optional<Answer> optionalAnswer = answersDAO.findById(answer_id);
			if (optionalAnswer.isPresent()){
				Answer editedAnswer = optionalAnswer.get();
				editedAnswer.setAnswer(answer.getAnswer());
				answersDAO.save(editedAnswer);
				return new ResponseEntity<>(editedAnswer, HttpStatus.OK);
			}else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
	}

	@GetMapping("questions/{question_id}/answers")
	public ResponseEntity<List<Answer>> getAnswersByQuestionId(@PathVariable("question_id") Long questionId){
		return returnCollectionIfNotEmpty(answersDAO.findByQuestion_Id(questionId));
	}

	@PostMapping("questions/{question_id}/answers")
	public ResponseEntity<Answer> newAnswer(@RequestBody Answer answer, @PathVariable("question_id") Long question_id){
		try {
			answer.setQuestion(questionsDAO.getOne(question_id));
			answersDAO.save(answer);
			return new ResponseEntity<>(answer, HttpStatus.OK);
		}catch (DataAccessException | ConstraintViolationException e){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("answers/{answer_id}")
	public ResponseEntity<Answer> deleteAnswer(@PathVariable("answer_id") Long answer_id){
		Optional<Answer> optionalAnswer = answersDAO.findById(answer_id);
		if (optionalAnswer.isPresent()){
			Answer answer = optionalAnswer.get();
			answersDAO.delete(answer);
			return new ResponseEntity<>(answer, HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}

}

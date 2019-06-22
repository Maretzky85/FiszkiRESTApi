package com.sikoramarek.fiszki.service;

import com.sikoramarek.fiszki.model.Answer;
import com.sikoramarek.fiszki.repository.AnswerRepository;
import com.sikoramarek.fiszki.repository.QuestionRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AnswerService {

	private AnswerRepository answerRepository;
	private QuestionRepository questionRepository;

	@Autowired
	public AnswerService(AnswerRepository answerRepository,
	                     QuestionRepository questionRepository) {
		this.answerRepository = answerRepository;
		this.questionRepository = questionRepository;
	}

	public ResponseEntity<List<Answer>> getAllAnswers() {
		return new ResponseEntity<>(answerRepository.findAll(), HttpStatus.OK);
	}

	public ResponseEntity<List<Answer>> getAnswerById(Long answerId) {
		Optional<Answer> optionalAnswer = answerRepository.findById(answerId);
		if (optionalAnswer.isPresent()) {
			return new ResponseEntity<>(Collections.singletonList(answerRepository.findById(answerId).get()), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	public ResponseEntity<Answer> editAnswerById(Answer answer, Long answer_id) {
		Optional<Answer> optionalAnswer = answerRepository.findById(answer_id);
		if (optionalAnswer.isPresent()) {
			Answer editedAnswer = optionalAnswer.get();
			editedAnswer.setAnswer(answer.getAnswer());
			answerRepository.save(editedAnswer);
			return new ResponseEntity<>(editedAnswer, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	public ResponseEntity<List<Answer>> getAnswersByQuestionId(Long questionId) {
		List<Answer> answers = answerRepository.findByQuestion_Id(questionId);
		return new ResponseEntity<>(answers, HttpStatus.OK);
	}

	public ResponseEntity<Answer> newAnswer(Answer answer, Long question_id) {
		try {
			answer.setQuestion(questionRepository.getOne(question_id));
			answerRepository.save(answer);
			return new ResponseEntity<>(answer, HttpStatus.OK);
		} catch (DataAccessException | ConstraintViolationException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	public ResponseEntity<Answer> deleteAnswer(Long answer_id) {
		Optional<Answer> optionalAnswer = answerRepository.findById(answer_id);
		if (optionalAnswer.isPresent()) {
			Answer answer = optionalAnswer.get();
			answerRepository.delete(answer);
			return new ResponseEntity<>(answer, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}


}

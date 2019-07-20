package com.sikoramarek.fiszki.service;

import com.sikoramarek.fiszki.model.Answer;
import com.sikoramarek.fiszki.repository.AnswerRepository;
import com.sikoramarek.fiszki.repository.QuestionRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Collection;
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

	public ResponseEntity<Collection> getUserAnswers(String userName){
		if (checkForAdmin()) {
			Collection<Answer> answers = answerRepository.findAnswersByUserName(userName);
			return new ResponseEntity<>(answers, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	}

	public ResponseEntity<Answer> editAnswerById(Answer newAnswer, Long answer_id, Principal principal) {
		if (principal == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Answer> optionalAnswer = answerRepository.findById(answer_id);
		if (!optionalAnswer.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Answer answer = optionalAnswer.get();
		if (!(principal.getName().equals(answer.getUser()) || checkForAdmin())) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		answer.setAnswer(newAnswer.getAnswer());
		try {
			answerRepository.save(answer);
		} catch (DataAccessException | javax.validation.ConstraintViolationException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(answer, HttpStatus.OK);
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

	public ResponseEntity<Answer> deleteAnswer(Long answer_id, Principal principal) {
		if (principal == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Answer> optionalAnswer = answerRepository.findById(answer_id);
		if (!optionalAnswer.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Answer answer = optionalAnswer.get();
		if (!(principal.getName().equals(answer.getUser()) || checkForAdmin())) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		answerRepository.delete(answer);
		return new ResponseEntity<>(answer, HttpStatus.OK);
	}

	private boolean checkForAdmin() {
		Collection<? extends GrantedAuthority> authority = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		if (authority.stream().anyMatch(o -> o.getAuthority().equals("ROLE_ADMIN"))) {
			return true;
		} else {
			return false;
		}
	}
}

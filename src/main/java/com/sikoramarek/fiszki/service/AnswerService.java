package com.sikoramarek.fiszki.service;

import com.sikoramarek.fiszki.errors.BadRequestError;
import com.sikoramarek.fiszki.errors.NoPermissionError;
import com.sikoramarek.fiszki.errors.NotFoundError;
import com.sikoramarek.fiszki.errors.NotLoggedError;
import com.sikoramarek.fiszki.model.Answer;
import com.sikoramarek.fiszki.model.projections.AnswerOnly;
import com.sikoramarek.fiszki.repository.AnswerRepository;
import com.sikoramarek.fiszki.repository.QuestionRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.sikoramarek.fiszki.service.authentication.SecurityConstants.checkForAdmin;

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

	public ResponseEntity<Collection<AnswerOnly>> getUserAnswers(String userName){
		if (checkForAdmin()) {
			Collection<AnswerOnly> answers = answerRepository.findAnswersByUser(userName);
			return new ResponseEntity<>(answers, HttpStatus.OK);
		} else {
			throw new NoPermissionError("Only for administrators");
		}
	}

	public ResponseEntity<Answer> editAnswerById(Answer newAnswer, Long answer_id, Principal principal) {
		Answer answer = checkForPermissionsAndExistence(answer_id, principal);
		if (newAnswer.getAnswer().length() == 0) {
			throw new BadRequestError("Answer must be unique and not null");
		}
		answer.setAnswer(newAnswer.getAnswer());
		try {
			answerRepository.save(answer);
		} catch (DataAccessException | javax.validation.ConstraintViolationException e) {
			throw new BadRequestError("Answer must be unique and not null");
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
			throw new BadRequestError("Answer must be unique and not null");
		}
	}

	public ResponseEntity<Answer> deleteAnswer(Long answer_id, Principal principal) {
		Answer answer = checkForPermissionsAndExistence(answer_id, principal);
		answerRepository.delete(answer);
		return new ResponseEntity<>(answer, HttpStatus.OK);
	}

	private Answer checkForPermissionsAndExistence(Long answerId, Principal principal){
		if (principal == null) {
			throw new NotLoggedError("Only for logged users");
		}
		Optional<Answer> optionalAnswer = answerRepository.findById(answerId);
		if (!optionalAnswer.isPresent()) {
			throw new NotFoundError("Answer of ID " + " not found");
		}
		Answer answer = optionalAnswer.get();
		if (!(principal.getName().equals(answer.getUser()) || checkForAdmin())) {
			throw new NoPermissionError("You have to be owner or administrator");
		}
		return answer;
	}

}

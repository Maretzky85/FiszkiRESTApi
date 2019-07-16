package com.sikoramarek.fiszki.service;

import com.sikoramarek.fiszki.model.Answer;
import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.Tag;
import com.sikoramarek.fiszki.model.UserModel;
import com.sikoramarek.fiszki.repository.AnswerRepository;
import com.sikoramarek.fiszki.repository.QuestionRepository;
import com.sikoramarek.fiszki.repository.TagRepository;
import com.sikoramarek.fiszki.repository.UserRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionService {

	private QuestionRepository questionsRepository;
	private AnswerRepository answersRepository;
	private TagRepository tagRepository;
	private UserRepository userRepository;

	public QuestionService(QuestionRepository questionsRepository,
	                       TagRepository tagRepository,
	                       AnswerRepository answersRepository,
	                       UserRepository userRepository) {

		this.questionsRepository = questionsRepository;
		this.answersRepository = answersRepository;
		this.tagRepository = tagRepository;
		this.userRepository = userRepository;
	}

	private boolean checkForAdmin() {
		Collection<? extends GrantedAuthority> authority = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		if (authority.stream().anyMatch(o -> o.getAuthority().equals("ROLE_ADMIN"))) {
			return true;
		} else {
			return false;
		}
	}

	public Page<Question> getPageableQuestions(int page, int size) {
		if (checkForAdmin()) {
			return questionsRepository.findAll(PageRequest.of(page, size));
		}
		return questionsRepository.findAllByAcceptedTrue(PageRequest.of(page, size));
	}

	public ResponseEntity<List<Question>> getQuestionById(Long question_id) {
		Optional<Question> optionalQuestion = questionsRepository.findQuestionById(question_id);
		return optionalQuestion
				.map(question ->
						new ResponseEntity<>(Collections.singletonList(question), HttpStatus.OK))
				.orElseGet(() ->
						new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	public ResponseEntity<Question> newQuestion(Question question) {
		questionsRepository.save(question);
		if (question.getAnswers() != null && question.getAnswers().size() > 0) {
			try {
				Collection<Answer> answers = question.getAnswers();
				answers.forEach(answer -> {
					answer.setQuestion(question);
					answer.setUser(question.getUser());
					answersRepository.save(answer);
				});
			} catch (DataAccessException | ConstraintViolationException e) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<>(question, HttpStatus.OK);
	}

	public ResponseEntity<Question> editQuestion(Question newQuestion, Long question_id, Principal principal) {
		if (principal == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Question> optionalQuestion = questionsRepository.findById(question_id);
		if (!optionalQuestion.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Question question = optionalQuestion.get();
		if (!principal.getName().equals(question.getUser())) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		for (Tag tag : newQuestion.getTags()) {
			if (!tagRepository.existsById(tag.getId())) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}
		question.setTags(newQuestion.getTags());
		question.setTitle(newQuestion.getTitle());
		question.setQuestion(newQuestion.getQuestion());
		try {
			questionsRepository.save(question);
		} catch (DataAccessException | ConstraintViolationException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(question, HttpStatus.OK);
	}

	public ResponseEntity<Question> deleteQuestion(Long question_id, Principal principal) {
		if (principal == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Question> optionalQuestion = questionsRepository.findById(question_id);
		if (!optionalQuestion.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Question question = optionalQuestion.get();
		if (!principal.getName().equals(question.getUser())) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		questionsRepository.delete(question);
		return new ResponseEntity<>(question, HttpStatus.OK);
	}

	public ResponseEntity<List<Question>> getRandom(Principal principal) {
		Page<Question> questionPage;
		if (principal != null && getCurrentUserKnownQuestionIds().size() > 0) {
			Long quantity = questionsRepository
					.countQuestionByAcceptedTrueAndIdNotIn(
							getCurrentUserKnownQuestionIds());
			int index = (int) (Math.random() * quantity);
			questionPage = questionsRepository
					.findQuestionsByIdNotIn(
							getCurrentUserKnownQuestionIds(),
							PageRequest.of(index, 1, Sort.unsorted()));
		} else {
			Long quantity = questionsRepository.countByAcceptedTrue();
			int index = (int) (Math.random() * quantity);
			questionPage = questionsRepository.findAllByAcceptedTrue(PageRequest.of(index, 1, Sort.unsorted()));
		}
		if (questionPage.hasContent()) {
			return new ResponseEntity<>(Collections.singletonList(questionPage.getContent().get(0)), HttpStatus.OK);
		}
		return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
	}

	private Collection<Long> getCurrentUserKnownQuestionIds() {
		String userName = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Collection<Question> knownQuestions = userRepository.getUserByUsername(userName).getKnownQuestions();
		return knownQuestions.stream().map(Question::getId).collect(Collectors.toList());
	}

	public ResponseEntity markQuestionAsKnown(Principal principal, Long question_id) {
		if (principal != null) {
			String userName = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			UserModel user = userRepository.getUserByUsername(userName);
			Optional<Question> optionalQuestion = questionsRepository.findQuestionById(question_id);
			if (optionalQuestion.isPresent()) {
				Question question = optionalQuestion.get();
				Set<UserModel> usersKnownThisQuestion = question.getUsersKnownThisQuestion();
				if (usersKnownThisQuestion.contains(user)) {
					usersKnownThisQuestion.remove(user);
				} else {
					usersKnownThisQuestion.add(user);
				}
				questionsRepository.save(question);
				return new ResponseEntity(HttpStatus.OK);
			} else {
				return new ResponseEntity(HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity(HttpStatus.UNAUTHORIZED);
		}
	}

	public ResponseEntity<Collection<Question>> getKnownQuestions(Principal principal) {
		if (principal == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		Long userId = userRepository.getId(principal.getName());
		Collection<Question> knownQuestions =
				questionsRepository
						.findQuestionsByUsersKnownThisQuestion(userId);
		return new ResponseEntity<>(knownQuestions, HttpStatus.OK);
	}
}

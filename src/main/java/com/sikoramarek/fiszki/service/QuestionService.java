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

	private QuestionRepository questionRepository;
	private AnswerRepository answerRepository;
	private TagRepository tagRepository;
	private UserRepository userRepository;

	public QuestionService(QuestionRepository questionRepository,
	                       TagRepository tagRepository,
	                       AnswerRepository answerRepository,
	                       UserRepository userRepository) {

		this.questionRepository = questionRepository;
		this.answerRepository = answerRepository;
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
			return questionRepository.findAll(PageRequest.of(page, size));
		}
		return questionRepository.findAllByAcceptedTrue(PageRequest.of(page, size));
	}

	public ResponseEntity<List<Question>> getQuestionById(Long question_id) {
		Optional<Question> optionalQuestion = questionRepository.findQuestionById(question_id);
		return optionalQuestion
				.map(question ->
						new ResponseEntity<>(Collections.singletonList(question), HttpStatus.OK))
				.orElseGet(() ->
						new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	public ResponseEntity<Question> newQuestion(Question question) {
		questionRepository.save(question);
		if (question.getAnswers() != null && question.getAnswers().size() > 0) {
			try {
				Collection<Answer> answers = question.getAnswers();
				answers.forEach(answer -> {
					answer.setQuestion(question);
					answer.setUser(question.getUser());
					answerRepository.save(answer);
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
		Optional<Question> optionalQuestion = questionRepository.findById(question_id);
		if (!optionalQuestion.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Question question = optionalQuestion.get();
		if (!(principal.getName().equals(question.getUser()) || checkForAdmin())) {
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
			questionRepository.save(question);
		} catch (DataAccessException | ConstraintViolationException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(question, HttpStatus.OK);
	}

	public ResponseEntity<Question> deleteQuestion(Long question_id, Principal principal) {
		if (principal == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Question> optionalQuestion = questionRepository.findById(question_id);
		if (!optionalQuestion.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Question question = optionalQuestion.get();
		if (!(principal.getName().equals(question.getUser()) || checkForAdmin())) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		questionRepository.delete(question);
		return new ResponseEntity<>(question, HttpStatus.OK);
	}

	public ResponseEntity<List<Question>> getRandom(Principal principal) {
		Page<Question> questionPage;
		if (principal != null && getCurrentUserKnownQuestionIds().size() > 0) {
			Long quantity = questionRepository
					.countQuestionByAcceptedTrueAndIdNotIn(
							getCurrentUserKnownQuestionIds());
			int index = (int) (Math.random() * quantity);
			questionPage = questionRepository
					.findQuestionsByIdNotIn(
							getCurrentUserKnownQuestionIds(),
							PageRequest.of(index, 1, Sort.unsorted()));
		} else {
			Long quantity = questionRepository.countByAcceptedTrue();
			int index = (int) (Math.random() * quantity);
			questionPage = questionRepository.findAllByAcceptedTrue(PageRequest.of(index, 1, Sort.unsorted()));
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
			Optional<Question> optionalQuestion = questionRepository.findQuestionById(question_id);
			if (optionalQuestion.isPresent()) {
				Question question = optionalQuestion.get();
				Set<UserModel> usersKnownThisQuestion = question.getUsersKnownThisQuestion();
				if (usersKnownThisQuestion.contains(user)) {
					usersKnownThisQuestion.remove(user);
				} else {
					usersKnownThisQuestion.add(user);
				}
				questionRepository.save(question);
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
				questionRepository
						.findQuestionsByUsersKnownThisQuestion(userId);
		return new ResponseEntity<>(knownQuestions, HttpStatus.OK);
	}

	public ResponseEntity<Collection<Question>> getQuestionsByTagId(Long tagId) {
		Optional<Tag> optionalTag = tagRepository.findById(tagId);
		if (optionalTag.isPresent()) {
			List<Question> questionList = questionRepository.findQuestionsByTagsContaining(optionalTag.get());
			return new ResponseEntity<>(questionList, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	public ResponseEntity<List<Question>> getRandomByTag(Principal principal, Long tag_id) {
		Page<Question> questionPage;
		Tag tag;
		Optional<Tag> optionalTag = tagRepository.findById(tag_id);
		if (optionalTag.isPresent()) {
			tag = optionalTag.get();
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		if (principal != null && getCurrentUserKnownQuestionIds().size() > 0) {
			Long quantity = questionRepository
					.countQuestionsByTagsContainingAndAcceptedTrueAndIdNotIn(
							tag, getCurrentUserKnownQuestionIds());
			int index = (int) (Math.random() * quantity);
			questionPage = questionRepository.findQuestionsByIdNotInAndAcceptedTrueAndTagsContaining(
					getCurrentUserKnownQuestionIds(), tag, PageRequest.of(index, 1, Sort.unsorted()));
		} else {
			Long quantity = questionRepository.countQuestionByTagsContainingAndAcceptedTrue(tag);
			int index = (int) (Math.random() * quantity);
			questionPage = questionRepository
					.findAllByTagsContainingAndAcceptedTrue(tag, PageRequest.of(index, 1, Sort.unsorted()));
		}

		if (questionPage.hasContent()) {
			return new ResponseEntity<>(Collections.singletonList(questionPage.getContent().get(0)), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
	}
}

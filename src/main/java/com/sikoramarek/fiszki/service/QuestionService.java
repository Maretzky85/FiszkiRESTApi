package com.sikoramarek.fiszki.service;

import com.sikoramarek.fiszki.errors.BadRequestError;
import com.sikoramarek.fiszki.errors.NoPermissionError;
import com.sikoramarek.fiszki.errors.NotFoundError;
import com.sikoramarek.fiszki.errors.NotLoggedError;
import com.sikoramarek.fiszki.model.Answer;
import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.Tag;
import com.sikoramarek.fiszki.model.UserModel;
import com.sikoramarek.fiszki.model.projections.QuestionOnly;
import com.sikoramarek.fiszki.repository.AnswerRepository;
import com.sikoramarek.fiszki.repository.QuestionRepository;
import com.sikoramarek.fiszki.repository.TagRepository;
import com.sikoramarek.fiszki.repository.UserRepository;
import com.sikoramarek.fiszki.model.projections.QuestionOnly;
import com.sikoramarek.fiszki.repository.*;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static com.sikoramarek.fiszki.service.authentication.SecurityConstants.checkForAdmin;

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

	public ResponseEntity<Question> acceptQuestion(long questionId) {
		if (!checkForAdmin()) {
			throw new NoPermissionError("Only for Administrators");
		}
		Optional<Question> optionalQuestion = questionRepository.findQuestionById(questionId);
		if (optionalQuestion.isPresent()) {
			Question question = optionalQuestion.get();
			question.setAccepted(true);
			questionRepository.save(question);
			return new ResponseEntity<>(question, HttpStatus.OK);
		}
		throw new NotFoundError("Question of ID " + questionId + " not found");
	}

	public ResponseEntity<Collection<QuestionOnly>> getUserQuestions(String userName) {
		if (checkForAdmin()) {
			Collection<QuestionOnly> questions = questionRepository.findQuestionsByUser(userName);
			return new ResponseEntity<>(questions, HttpStatus.OK);
		} else {
			throw new NoPermissionError("Only for Administrators");
		}
	}

	public Page<Question> getPageableQuestions(int page, int size) {
		if (checkForAdmin()) {
			return questionRepository.findAll(PageRequest.of(page, size));
		}
		return questionRepository.findQuestionsByAcceptedTrue(PageRequest.of(page, size));
	}

	public ResponseEntity<List<Question>> getQuestionById(Long question_id) {
		Optional<Question> optionalQuestion = questionRepository.findQuestionById(question_id);
		return optionalQuestion
				.map(question ->
						new ResponseEntity<>(Collections.singletonList(question), HttpStatus.OK))
				.orElseThrow(() -> new NotFoundError("Question with ID " + question_id + " not found"));
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
				throw new BadRequestError("Answer must be unique and not empty");
			}
		}
		return new ResponseEntity<>(question, HttpStatus.OK);
	}

	public ResponseEntity<Question> editQuestion(Question newQuestion, Long questionId, Principal principal) {
		Question question = checkForPermissionsAndExistence(questionId, principal);
		for (Tag tag : newQuestion.getTags()) {
			if (!tagRepository.existsById(tag.getId())) {
				throw new BadRequestError("Tag of ID " + tag.getId() + " not found");
			}
		}
		question.setTags(newQuestion.getTags());
		question.setTitle(newQuestion.getTitle());
		question.setQuestion(newQuestion.getQuestion());
		try {
			questionRepository.save(question);
		} catch (DataAccessException | ConstraintViolationException e) {
			throw new BadRequestError("Problem saving question, fields must not be empty");
		}
		return new ResponseEntity<>(question, HttpStatus.OK);
	}

	public ResponseEntity<Question> deleteQuestion(Long questionId, Principal principal) {
		Question question = checkForPermissionsAndExistence(questionId, principal);
		questionRepository.delete(question);
		return new ResponseEntity<>(question, HttpStatus.OK);
	}

	public ResponseEntity<List<Question>> getRandom(Principal principal) {
		Page<Question> questionPage;
		if (principal != null && getCurrentUserKnownQuestionIds(principal).size() > 0) {
			Long quantity = questionRepository
					.countQuestionByAcceptedTrueAndIdNotIn(
							getCurrentUserKnownQuestionIds(principal));
			int index = (int) (Math.random() * quantity);
			questionPage = questionRepository
					.findQuestionsByIdNotIn(
							getCurrentUserKnownQuestionIds(principal),
							PageRequest.of(index, 1, Sort.unsorted()));
		} else {
			Long quantity = questionRepository.countByAcceptedTrue();
			int index = (int) (Math.random() * quantity);
			questionPage = questionRepository.findQuestionsByAcceptedTrue(PageRequest.of(index, 1, Sort.unsorted()));
		}
		if (questionPage.hasContent()) {
			return new ResponseEntity<>(Collections.singletonList(questionPage.getContent().get(0)), HttpStatus.OK);
		}
		return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
	}

	private Collection<Long> getCurrentUserKnownQuestionIds(Principal principal) {
		Collection<Question> knownQuestions = userRepository.getUserByUsername(principal.getName()).getKnownQuestions();
		return knownQuestions.stream().map(Question::getId).collect(Collectors.toList());
	}

	public ResponseEntity markQuestionAsKnown(Principal principal, Long questionId) {
		if (principal != null) {
			UserModel user = userRepository.getUserByUsername(principal.getName());
			Optional<Question> optionalQuestion = questionRepository.findQuestionById(questionId);
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
				throw new NotFoundError("Question of ID " + questionId + " not found");
			}
		} else {
			throw new NotLoggedError("You must be logged in");
		}
	}

	public ResponseEntity<Collection<Question>> getKnownQuestions(Principal principal) {
		if (principal == null) {
			throw new NotLoggedError("You must be logged in");
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
			throw new NotFoundError("Tag of ID " + tagId + "not found");
		}
	}

	public ResponseEntity<List<Question>> getRandomByTag(Principal principal, Long tagId) {
		Page<Question> questionPage;
		Tag tag;
		Optional<Tag> optionalTag = tagRepository.findById(tagId);
		Collection<Long> knownQuestionsId = Collections.emptyList();
		if (optionalTag.isPresent()) {
			tag = optionalTag.get();
		} else {
			throw new NotFoundError("Tag of ID " + tagId + " not found");
		}
		if (principal != null){
			knownQuestionsId = getCurrentUserKnownQuestionIds(principal);
		}
		if (knownQuestionsId.size() > 0) {
			Long quantity = questionRepository
					.countQuestionsByTagsContainingAndAcceptedTrueAndIdNotIn(
							tag, knownQuestionsId);
			int index = (int) (Math.random() * quantity);
			questionPage = questionRepository.findQuestionsByIdNotInAndAcceptedTrueAndTagsContaining(
					knownQuestionsId, tag, PageRequest.of(index, 1, Sort.unsorted()));
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

	private Question checkForPermissionsAndExistence(Long questionId, Principal principal) {
		if (principal == null) {
			throw new NotLoggedError("Only for logged users");
		}
		Optional<Question> optionalQuestion = questionRepository.findById(questionId);
		if (!optionalQuestion.isPresent()) {
			throw new NotFoundError("Question of ID " + " not found");
		}
		Question question = optionalQuestion.get();
		if (!(principal.getName().equals(question.getUser()) || checkForAdmin())) {
			throw new NoPermissionError("You have to be owner or administrator");
		}
		return question;
	}
}

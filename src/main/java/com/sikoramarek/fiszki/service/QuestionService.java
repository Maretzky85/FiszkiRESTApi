package com.sikoramarek.fiszki.service;

import com.sikoramarek.fiszki.model.Answer;
import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.Tag;
import com.sikoramarek.fiszki.repository.AnswerRepository;
import com.sikoramarek.fiszki.repository.QuestionRepository;
import com.sikoramarek.fiszki.repository.TagRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

	private QuestionRepository questionsRepository;
	private AnswerRepository answersRepository;
	private TagRepository tagRepository;

	public QuestionService(QuestionRepository questionsRepository,
	                       TagRepository tagRepository,
	                       AnswerRepository answersRepository) {

		this.questionsRepository = questionsRepository;
		this.answersRepository = answersRepository;
		this.tagRepository = tagRepository;

	}

	public Page<Question> getAllQuestions() {
		return questionsRepository.findAll(PageRequest.of(0, 10));
	}

	public Page<Question> getPageableQuestions(int page, int size) {
		return questionsRepository.findAll(PageRequest.of(page, size));
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
		if (question.getAnswers().size() > 0) {
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

	public ResponseEntity<Question> editQuestion(Question question, Long question_id) {
		if (questionsRepository.existsById(question.getId())) {
			for (Tag tag : question.getTags()
			) {
				if (!tagRepository.existsById(tag.getId())) {
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			}
			questionsRepository.save(question);
			return new ResponseEntity<>(question, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
		}
	}

	public ResponseEntity<Question> deleteQuestion(Long question_id) {
		Optional<Question> optionalQuestion = questionsRepository.findById(question_id);
		if (optionalQuestion.isPresent()) {
			Question question = optionalQuestion.get();
			questionsRepository.delete(question);
			return new ResponseEntity<>(question, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	public ResponseEntity<List<Question>> getRandom() {
		Long quantity = questionsRepository.count();
		int index = (int) (Math.random() * quantity);
		Page<Question> questionPage = questionsRepository.findAll(PageRequest.of(index, 1, Sort.unsorted()));
		if (questionPage.hasContent()) {
			return new ResponseEntity<>(Collections.singletonList(questionPage.getContent().get(0)), HttpStatus.OK);
		}
		return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
	}
}

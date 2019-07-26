package com.sikoramarek.fiszki.service;

import com.sikoramarek.fiszki.model.Answer;
import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.repository.AnswerRepository;
import com.sikoramarek.fiszki.repository.QuestionRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SearchService {

	QuestionRepository questionRepository;
	AnswerRepository answerRepository;

	@Autowired
	public SearchService(AnswerRepository answerRepository, QuestionRepository questionRepository) {
		this.questionRepository = questionRepository;
		this.answerRepository = answerRepository;
	}

	public ResponseEntity<Collection<Question>> search(String searchString){
		Set<Question> questionsResult = new HashSet<>(questionRepository
				.findQuestionsByQuestionLikeOrTitleLike(searchString, searchString));

		questionsResult.addAll(answerRepository.findAnswerByAnswerLike(searchString).stream()
			.map(answer -> answer.getQuestion().getId())
				.map(questionId ->
								(Question) Hibernate.unproxy(questionRepository.getOne(questionId))
										).collect(Collectors.toSet()));

		return new ResponseEntity<>(questionsResult, HttpStatus.OK);
	}
}

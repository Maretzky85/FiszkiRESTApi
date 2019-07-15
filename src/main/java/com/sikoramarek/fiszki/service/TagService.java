package com.sikoramarek.fiszki.service;

import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.Tag;
import com.sikoramarek.fiszki.repository.QuestionRepository;
import com.sikoramarek.fiszki.repository.TagRepository;
import com.sikoramarek.fiszki.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TagService {

	private TagRepository tagRepository;
	private QuestionRepository questionRepository;
	private UserRepository userRepository;

	public TagService(TagRepository tagRepository,
	                  QuestionRepository questionRepository,
	                  UserRepository userRepository) {
		this.tagRepository = tagRepository;
		this.questionRepository = questionRepository;
		this.userRepository = userRepository;
	}

	public ResponseEntity<Collection<Tag>> getAll() {
		List<Tag> tags = tagRepository.findAll();
		return new ResponseEntity<>(tags, HttpStatus.OK);
	}

	public ResponseEntity<Collection<Tag>> getById(Long tagId) {
		Optional<Tag> optionalTag = tagRepository.findById(tagId);
		return optionalTag.<ResponseEntity<Collection<Tag>>>map(
				tag -> new ResponseEntity<>(Collections.singleton(tag), HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	public ResponseEntity<Collection<Tag>> newTag(Tag tag) {
		if (tag.getTagName().length() == 0) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		if (tagRepository.findTagByTagNameEquals(tag.getTagName()).isPresent()) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		} else {
			tagRepository.save(tag);
			return new ResponseEntity<>(Collections.singleton(tag), HttpStatus.CREATED);
		}
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

	public ResponseEntity<Collection<Tag>> editTag(Tag newTag, Long tagId) {
		if (newTag.getTagName().length() == 0){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		if (tagRepository.findTagByTagNameEquals(newTag.getTagName()).isPresent()){
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		Optional<Tag> tag = tagRepository.findById(tagId);
		if (tag.isPresent()) {
			Tag tagToEdit = tag.get();
			tagToEdit.setTagName(newTag.getTagName());
			tagRepository.save(tagToEdit);
			return new ResponseEntity<>(Collections.singleton(tagToEdit), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	public ResponseEntity<Tag> deleteTag(Long tagId) {
		try {
			tagRepository.deleteById(tagId);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
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

	private Collection<Long> getCurrentUserKnownQuestionIds() {
		String userName = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Collection<Question> knownQuestions = userRepository.getUserByUsername(userName).getKnownQuestions();
		return knownQuestions.stream().map(Question::getId).collect(Collectors.toList());
	}

}

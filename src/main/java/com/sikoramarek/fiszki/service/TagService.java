package com.sikoramarek.fiszki.service;

import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.Tag;
import com.sikoramarek.fiszki.repository.QuestionRepository;
import com.sikoramarek.fiszki.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TagService {

	private TagRepository tagRepository;
	private QuestionRepository questionRepository;

	public TagService(TagRepository tagRepository,
	                  QuestionRepository questionRepository) {
		this.tagRepository = tagRepository;
		this.questionRepository = questionRepository;
	}

	public ResponseEntity<List<Tag>> getAll() {
		List<Tag> tags = tagRepository.findAll();
		if (!tags.isEmpty()) {
			return new ResponseEntity<>(tags, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.OK);
		}
	}

	public ResponseEntity<List<Tag>> getById(Long tagId) {
		Optional<Tag> optionalTag = tagRepository.findById(tagId);
		return optionalTag
				.map(tag -> new ResponseEntity<>(Collections.singletonList(tag), HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	public ResponseEntity<Tag> newTag(Tag tag) {
		if (tagRepository.findTagByTagNameEquals(tag.getTagName()).isPresent()) {
			return new ResponseEntity<>(tag, HttpStatus.NOT_MODIFIED);
		} else {
			tagRepository.save(tag);
			return new ResponseEntity<>(tag, HttpStatus.OK);
		}
	}

	public ResponseEntity<List<Question>> getQuestionsByTagId(Long tagId) {
		Optional<Tag> optionalTag = tagRepository.findById(tagId);
		if (optionalTag.isPresent()) {
			List<Question> questionList = questionRepository.findQuestionsByTagsContaining(optionalTag.get());
			return new ResponseEntity<>(questionList, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	public ResponseEntity<Tag> editTag(Tag newTag, Long tagId) {
		Optional<Tag> tag = tagRepository.findById(tagId);
		if (tag.isPresent()) {
			Tag tagToEdit = tag.get();
			tagToEdit.setTagName(newTag.getTagName());
			tagRepository.save(tagToEdit);
			return new ResponseEntity<>(tagToEdit, HttpStatus.OK);
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

	public ResponseEntity<List<Question>> getRandomByTag(Long tag_id) {
		Tag tag;
		Optional<Tag> optionalTag = tagRepository.findById(tag_id);
		if (optionalTag.isPresent()) {
			tag = optionalTag.get();
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Long quantity = questionRepository.countQuestionByTagsContaining(tag);
		int index = (int) (Math.random() * quantity);
		Page<Question> questionPage = questionRepository
				.findAllByTagsContainingAndAcceptedTrue(tag, PageRequest.of(index, 1, Sort.unsorted()));
		if (questionPage.hasContent()) {
			return new ResponseEntity<>(Collections.singletonList(questionPage.getContent().get(0)), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
	}

}

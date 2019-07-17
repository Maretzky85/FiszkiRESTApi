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
	private UserRepository userRepository;

	public TagService(TagRepository tagRepository,
	                  UserRepository userRepository) {
		this.tagRepository = tagRepository;
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

	private Collection<Long> getCurrentUserKnownQuestionIds() {
		String userName = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Collection<Question> knownQuestions = userRepository.getUserByUsername(userName).getKnownQuestions();
		return knownQuestions.stream().map(Question::getId).collect(Collectors.toList());
	}

}

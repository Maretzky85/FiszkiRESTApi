package com.sikoramarek.fiszki.service;

import com.sikoramarek.fiszki.errors.BadRequestError;
import com.sikoramarek.fiszki.errors.ConflictError;
import com.sikoramarek.fiszki.errors.NotFoundError;
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

import static com.sikoramarek.fiszki.service.authentication.SecurityConstants.checkForAdmin;

@Service
public class TagService {

	private TagRepository tagRepository;

	public TagService(TagRepository tagRepository) {
		this.tagRepository = tagRepository;
	}

	public ResponseEntity<Collection<Tag>> getAll() {
		List<Tag> tags = tagRepository.findAll();
		return new ResponseEntity<>(tags, HttpStatus.OK);
	}

	public ResponseEntity<Collection<Tag>> getById(Long tagId) {
		Optional<Tag> optionalTag = tagRepository.findById(tagId);
		return optionalTag.<ResponseEntity<Collection<Tag>>>map(
				tag -> new ResponseEntity<>(Collections.singleton(tag), HttpStatus.OK))
				.orElseThrow(() -> new NotFoundError("Tag of ID " + tagId + " not found"));
	}

	public ResponseEntity<Collection<Tag>> newTag(Tag tag) {
		if (tag.getTagName().length() == 0) {
			throw new BadRequestError("Tag name must not be empty");
		}
		if (tagRepository.findTagByTagNameEquals(tag.getTagName()).isPresent()) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		} else {
			tagRepository.save(tag);
			return new ResponseEntity<>(Collections.singleton(tag), HttpStatus.CREATED);
		}
	}

	public ResponseEntity<Collection<Tag>> editTag(Tag newTag, Long tagId) {
		checkForAdmin();
		if (newTag.getTagName().length() == 0){
			throw new BadRequestError("Tag name must not be empty");
		}
		if (tagRepository.findTagByTagNameEquals(newTag.getTagName()).isPresent()){
			throw new ConflictError(newTag.getTagName() + " exists");
		}
		Optional<Tag> tag = tagRepository.findById(tagId);
		if (tag.isPresent()) {
			Tag tagToEdit = tag.get();
			tagToEdit.setTagName(newTag.getTagName());
			tagRepository.save(tagToEdit);
			return new ResponseEntity<>(Collections.singleton(tagToEdit), HttpStatus.OK);
		} else {
			throw new NotFoundError("Tag of ID " + tagId + "not found");
		}
	}

	public ResponseEntity<Tag> deleteTag(Long tagId) {
		try {
			tagRepository.deleteById(tagId);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			throw new NotFoundError("Tag of ID " + tagId + "not found");
		}
	}
}

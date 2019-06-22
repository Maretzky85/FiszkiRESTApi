package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.model.Answer;
import com.sikoramarek.fiszki.repository.QuestionRepository;
import com.sikoramarek.fiszki.repository.TagRepository;
import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.Tag;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class TagController extends AbstractController {

	private TagRepository tagDAO;
	private QuestionRepository questionsDAO;

	public TagController(
			@Autowired TagRepository tagDAO,
			@Autowired QuestionRepository questionsDAO){
		this.tagDAO = tagDAO;
		this.questionsDAO = questionsDAO;
	}


	@GetMapping("tags")
	public ResponseEntity<List<Tag>> getAllTags(){
		return returnCollectionIfNotEmpty(tagDAO.findAll());
	}


	@GetMapping("tags/{tagId}")
	public ResponseEntity<ArrayList<Tag>> getTagById(@PathVariable("tagId") Long tagId){
		return returnIfNotEmpty(tagDAO.findById(tagId));
	}


	@GetMapping("tags/{tagId}/questions")
	public ResponseEntity<List<Question>> getQuestionsByTagId(@PathVariable("tagId") Long tagId) {
		Optional<Tag> tag = tagDAO.findById(tagId);
		if (tag.isPresent()){
			List<Question> questionList = questionsDAO.findQuestionsByTagsContaining(tag.get());
			return new ResponseEntity<>(questionList, HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("tags")
	public ResponseEntity<Tag> newTag(@RequestBody Tag tag){
		try {
			tagDAO.save(tag);
			return new ResponseEntity<>(tag, HttpStatus.OK);
		}catch (DataAccessException | ConstraintViolationException e){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("tags/{tag_id}")
	public ResponseEntity<Tag> editTag(@PathVariable("tag_id") Long tagId, @RequestBody Tag newTag){
		Optional<Tag> tag = tagDAO.findById(tagId);
		if (tag.isPresent()){
			Tag tagToEdit = tag.get();
			tagToEdit.setTagName(newTag.getTagName());
			tagDAO.save(tagToEdit);
			return new ResponseEntity<>(tagToEdit, HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
		}
	}

	@DeleteMapping("tags/{tag_id}")
	public ResponseEntity<Answer> deleteAnswer(@PathVariable("tag_id") Long tag_id){
		try {
			tagDAO.deleteById(tag_id);
			return new ResponseEntity<>(HttpStatus.OK);
		}catch (Exception e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("tags/{tag_id}/questions/random")
	public ResponseEntity<ArrayList<Question>> getRandom(@PathVariable("tag_id") Long tag_id){
		Tag tag;
		Optional<Tag> optionalTag = tagDAO.findById(tag_id);
		if (optionalTag.isPresent()){
			tag = optionalTag.get();
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Long quantity = questionsDAO.countQuestionByTagsContaining(tag);
		int index = (int)(Math.random() * quantity);
		Page<Question> questionPage = questionsDAO
				.findAllByTagsContaining(tag, PageRequest.of(index, 1, Sort.unsorted()));
		if (questionPage.hasContent()) {
			return new ResponseEntity<>(packToArray(questionPage.getContent().get(0)), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
	}
}

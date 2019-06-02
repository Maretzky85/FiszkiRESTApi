package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.model.Answer;
import com.sikoramarek.fiszki.model.DAO.QuestionsDAO;
import com.sikoramarek.fiszki.model.DAO.TagDAO;
import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.Tag;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class TagController extends ReturnController{

	private TagDAO tagDAO;
	private QuestionsDAO questionsDAO;

	public TagController(
			@Autowired TagDAO tagDAO,
			@Autowired QuestionsDAO questionsDAO){
		this.tagDAO = tagDAO;
		this.questionsDAO = questionsDAO;
	}


	@GetMapping("tags")
	public ResponseEntity<List<Tag>> getAllTags(){
		return returnCollectionIfNotEmpty(tagDAO.findAll());
	}


	@GetMapping("tags/{tagId}")
	public ResponseEntity<Tag> getTagById(@PathVariable("tagId") Long tagId){
		return returnIfNotEmpty(tagDAO.findById(tagId));
	}


	@GetMapping("tags/{tagId}/questions")
	public ResponseEntity<List<Question>> getQuestionsByTagId(@PathVariable("tagId") Long tagId) {
		Optional<Tag> tag = tagDAO.findById(tagId);
		if (tag.isPresent()){
			return returnCollectionIfNotEmpty(questionsDAO.findQuestionsByTagsContaining(tag.get()));
		}else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("tags/")
	public ResponseEntity<Tag> newAnswer(@RequestBody Tag tag){
		try {
			tagDAO.save(tag);
			return new ResponseEntity<>(tag, HttpStatus.OK);
		}catch (DataAccessException | ConstraintViolationException e){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PatchMapping("tags/{tag_id}")
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
	public ResponseEntity<Answer> deleteAnswer(@PathVariable("tag_id") Long answer_id){
		try {
			tagDAO.deleteById(answer_id);
			return new ResponseEntity<>(HttpStatus.OK);
		}catch (Exception e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}

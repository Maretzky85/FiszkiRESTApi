package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.Tag;
import com.sikoramarek.fiszki.service.QuestionService;
import com.sikoramarek.fiszki.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

@RestController
public class TagController {

	private TagService tagService;
	private QuestionService questionService;

	@Autowired
	public TagController(
			TagService tagService, QuestionService questionService) {
		this.tagService = tagService;
		this.questionService = questionService;
	}


	@GetMapping("tags")
	public ResponseEntity<Collection<Tag>> getAllTags() {
		return tagService.getAll();
	}


	@GetMapping("tags/{tagId}")
	public ResponseEntity<Collection<Tag>> getTagById(@PathVariable("tagId") Long tagId) {
		return tagService.getById(tagId);
	}


	@GetMapping("tags/{tagId}/questions")
	public ResponseEntity<Collection<Question>> getQuestionsByTagId(@PathVariable("tagId") Long tagId) {
		return questionService.getQuestionsByTagId(tagId);
	}

	@PostMapping("tags")
	public ResponseEntity<Collection<Tag>> newTag(@RequestBody Tag tag) {
		return tagService.newTag(tag);
	}

	@PutMapping("tags/{tag_id}")
	public ResponseEntity<Collection<Tag>> editTag(@PathVariable("tag_id") Long tagId, @RequestBody Tag newTag) {
		return tagService.editTag(newTag, tagId);
	}

	@DeleteMapping("tags/{tagId}")
	public ResponseEntity<Tag> deleteTag(@PathVariable("tagId") Long tagId) {
		return tagService.deleteTag(tagId);
	}

	@GetMapping("tags/{tagId}/questions/random")
	public ResponseEntity<List<Question>> getRandom(Principal principal, @PathVariable("tagId") Long tagId) {
		return questionService.getRandomByTag(principal, tagId);
	}
}

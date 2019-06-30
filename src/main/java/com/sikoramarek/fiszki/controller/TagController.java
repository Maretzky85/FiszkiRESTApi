package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.model.Tag;
import com.sikoramarek.fiszki.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TagController{

	private TagService tagService;

	@Autowired
	public TagController(
			TagService tagService){
		this.tagService = tagService;
	}


	@GetMapping("tags")
	public ResponseEntity<List<Tag>> getAllTags(){
		return tagService.getAll();
	}


	@GetMapping("tags/{tagId}")
	public ResponseEntity<List<Tag>> getTagById(@PathVariable("tagId") Long tagId){
		return tagService.getById(tagId);
	}


	@GetMapping("tags/{tagId}/questions")
	public ResponseEntity<List<Question>> getQuestionsByTagId(@PathVariable("tagId") Long tagId) {
		return tagService.getQuestionsByTagId(tagId);
	}

	@PostMapping("tags")
	public ResponseEntity<Tag> newTag(@RequestBody Tag tag){
		return tagService.newTag(tag);
	}

	@PutMapping("tags/{tag_id}")
	public ResponseEntity<Tag> editTag(@PathVariable("tag_id") Long tagId, @RequestBody Tag newTag){
		return tagService.editTag(newTag, tagId);
	}

	@DeleteMapping("tags/{tagId}")
	public ResponseEntity<Tag> deleteTag(@PathVariable("tagId") Long tagId){
		return tagService.deleteTag(tagId);
	}

	@GetMapping("tags/{tagId}/questions/random")
	public ResponseEntity<List<Question>> getRandom(@PathVariable("tagId") Long tagId){
		return tagService.getRandomByTag(tagId);
	}
}

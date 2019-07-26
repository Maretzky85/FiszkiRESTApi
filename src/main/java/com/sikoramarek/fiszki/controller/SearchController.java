package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.model.Question;
import com.sikoramarek.fiszki.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class SearchController {

	SearchService searchService;

	public SearchController(SearchService searchService) {
		this.searchService = searchService;
	}

	@GetMapping(value = "/search/{searchString}")
	public ResponseEntity<Collection<Question>> search(@PathVariable("searchString") String searchString) {
		searchString = "%"+searchString+"%";
		return searchService.search(searchString);
	}
}

package com.sikoramarek.fiszki.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.Optional;

abstract class ReturnController {

	<T> ResponseEntity<T> returnCollectionIfNotEmpty(T objectToCheck){
		if (Collection.class.isAssignableFrom(objectToCheck.getClass())){
			Collection collection = (Collection) objectToCheck;
			if (!collection.isEmpty()){
				return new ResponseEntity<>(objectToCheck, HttpStatus.OK);
			}
			else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		}

		else {
			throw new RuntimeException("Wrong class exception");
		}
	}

	<T> ResponseEntity<T> returnIfNotEmpty(Optional<T> objectToCheck){
		return objectToCheck.map(t -> new ResponseEntity<>(t, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

}

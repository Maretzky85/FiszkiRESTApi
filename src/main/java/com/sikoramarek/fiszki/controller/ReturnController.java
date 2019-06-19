package com.sikoramarek.fiszki.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
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

	<T> ResponseEntity<ArrayList<T>> returnIfNotEmpty(Optional<T> objectToCheck) {
		if (objectToCheck.isPresent()){
			return new ResponseEntity<>(packToArray(objectToCheck.get()), HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	<T> ArrayList<T> packToArray(T objectToPack){
		ArrayList<T> returnArray = new ArrayList<>(1);
		returnArray.add(objectToPack);
		return returnArray;
	}

}

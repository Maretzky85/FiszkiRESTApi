package com.sikoramarek.fiszki.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Already exists")
public class ConflictError extends RuntimeException{

	public ConflictError(){
		super();
	}

	public ConflictError(String msg){
		super(msg);
	}

}

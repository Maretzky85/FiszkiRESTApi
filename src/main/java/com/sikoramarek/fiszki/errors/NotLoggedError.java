package com.sikoramarek.fiszki.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class NotLoggedError extends RuntimeException{

	public NotLoggedError() {
		super("You have to be logged first");
	}
	public NotLoggedError(String msg) {
		super(msg);
	}

}

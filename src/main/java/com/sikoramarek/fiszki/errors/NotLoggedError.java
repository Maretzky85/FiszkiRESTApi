package com.sikoramarek.fiszki.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "You have to be logged first")
public class NotLoggedError extends RuntimeException{

	public NotLoggedError() {
		super();
	}
	public NotLoggedError(String msg) {
		super(msg);
	}

}

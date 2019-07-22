package com.sikoramarek.fiszki.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Request error")
public class BadRequestError extends RuntimeException{

	public BadRequestError() {
		super();
	}

	public BadRequestError(String msg) {
		super(msg);
	}

}

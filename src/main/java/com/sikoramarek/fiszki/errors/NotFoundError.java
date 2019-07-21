package com.sikoramarek.fiszki.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Item not found")
public class NotFoundError extends RuntimeException{

	public NotFoundError() {
		super();
	}

	public NotFoundError(String msg) {
		super(msg);
	}

}

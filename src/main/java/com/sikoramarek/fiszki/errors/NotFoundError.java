package com.sikoramarek.fiszki.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundError extends RuntimeException{

	public NotFoundError() {
		super("Item not found");
	}

	public NotFoundError(String msg) {
		super(msg);
	}

}

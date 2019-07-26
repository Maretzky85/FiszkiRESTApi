package com.sikoramarek.fiszki.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class NoPermissionError extends RuntimeException{

	public NoPermissionError(String message) {
		super(message);
	}

	public NoPermissionError() {
		super("Don`t have permission");
	}

}

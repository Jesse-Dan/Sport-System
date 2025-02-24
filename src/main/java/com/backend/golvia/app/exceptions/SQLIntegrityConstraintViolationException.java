package com.backend.golvia.app.exceptions;

public class SQLIntegrityConstraintViolationException extends RuntimeException {

	 public SQLIntegrityConstraintViolationException(String message) {
	        super(message);
	    }
}

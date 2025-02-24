package com.backend.golvia.app.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

public class ApiResponse<T> {

	private int status;
	private String message;
	private T data;
	private LocalDateTime timestamp;

	@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude 'errors' field if it's null
	private Object errors;

	public ApiResponse(int status, String message, T data, Object errors) {
		this.status = status;
		this.message = message;
		this.data = data;
		this.timestamp = LocalDateTime.now();
		this.errors = errors;
	}
	public ApiResponse(int status, String message, T data) {
		this.status = status;
		this.message = message;
		this.data = data;
		this.timestamp = LocalDateTime.now();
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public Object getErrors() {
		return errors;
	}

	public void setErrors(Object errors) {
		this.errors = errors;
	}

	// Getters and setters...
}
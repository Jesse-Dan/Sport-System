package com.backend.golvia.app.models.response;

import java.time.LocalDateTime;

public class AuthResponse<T>{
	
	
    private int status;
    private String message;
    public AuthResponse(int status, String token ,String message, Object data) {
		super();
		this.status = status;
		this.message = message;
		this.data = data;
		this.token = token;
		this.timestamp = LocalDateTime.now();
	}
	public AuthResponse() {
		super();
		// TODO Auto-generated constructor stub
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
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	private String token;
	private Object data;
    public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	private LocalDateTime timestamp;
   // private long expiresIn;


}

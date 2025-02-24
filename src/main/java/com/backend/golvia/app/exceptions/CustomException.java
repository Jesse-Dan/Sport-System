package com.backend.golvia.app.exceptions;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

import java.io.Serial;

public class CustomException extends RuntimeException {
//    private final int status;
    private int status;
    private int code;
    private Throwable cause;
    private String message;
    @Serial
    private static final long serialVersionUID = 1L;

    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }
    public CustomException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public CustomException(int status, String message, Throwable cause, int code) {
        this.status = status;
        this.message = message;
        this.code = code;
        this.cause = cause;
    }

    public CustomException(String message) {
    }
    public CustomException(String s, MalformedJwtException e) {
    }
    public CustomException(String s, ExpiredJwtException e) {
    }

    public CustomException(String s, SignatureException e) {
    }

    public int getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    @Override
    public String getMessage() {
        return message;
    }

}

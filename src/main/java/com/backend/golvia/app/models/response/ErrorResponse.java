package com.backend.golvia.app.models.response;

import java.time.LocalDateTime;

public class ErrorResponse {
    @SuppressWarnings("unused")
    private int status;
    @SuppressWarnings("unused")
    private String message;
    @SuppressWarnings("unused")
    private LocalDateTime timestamp;
    @SuppressWarnings("unused")
    private Object details;

    public ErrorResponse(int status, String message, LocalDateTime timestamp, Object details) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.details = details;
    }


    // Getters and setters...
}

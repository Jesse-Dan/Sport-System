package com.backend.golvia.app.models.response;

import com.backend.golvia.app.services.post.Pagination;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto<T> {
    private int status;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private Pagination pagination;



    // Ensure this constructor is public
    public ResponseDto(int status, String message, T data, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
    }
    // Ensure this constructor is public
    public ResponseDto(int status, String message, T data, LocalDateTime timestamp, Pagination pagination) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
        this.pagination= pagination;
    }

    // Constructor
    public ResponseDto(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}

package com.backend.golvia.app.exceptions;


import com.backend.golvia.app.models.response.ApiResponse;
import com.backend.golvia.app.models.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
public class CustomizedExceptionHandler extends ResponseEntityExceptionHandler {


    private static final Logger log = LoggerFactory.getLogger(CustomizedExceptionHandler.class);


    @ExceptionHandler(CustomException.class)
    public final ResponseEntity<ErrorResponse> handleCustomizedErrors(CustomException ex, WebRequest request) {
        log.error(ex.getMessage(), ex);
        log.error("Action taken from", request.getLocale().getCountry());
        ErrorResponse errorResponse = new ErrorResponse(400, ex.getMessage(), LocalDateTime.now(),null);
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(ex.getStatus()));
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String message = ex.getMessage();

        if (message.contains("empty String")) {
            message = "An empty string is not allowed for this field";
        } else if (message.toLowerCase().contains("datetime")) {
            message = "An invalid Date Format was provided";
        } else if (message.contains("enums.AddressVerificationStatus")) {
            message = "An invalid AddressVerificationStatus was provided";
        } else if (message.contains("boolean")) {
            message = "An invalid boolean value was provided. Allowed values are true/false";
        }

        ApiResponse<String> response = new ApiResponse<>(400, message, ex.getMessage(),null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiResponse<String> response = new ApiResponse<>(400, ex.getMessage(), ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }





    private String parseErrorMessage(String originalMessage) {
        if (originalMessage.contains("unique index") || originalMessage.contains("Unique index")) {
            return parseUniqueConstraintViolation(originalMessage);
        }

        if (originalMessage.contains("cannot be null") || originalMessage.contains("Cannot insert the value NULL into column")) {
            return parseNotNullConstraintViolation(originalMessage);
        }

        if (originalMessage.contains("foreign key constraint")) {
            return parseForeignKeyConstraintViolation(originalMessage);
        }

        if (originalMessage.contains("Duplicate entry")) {
            return parseDuplicateKeyUniqueConstraint(originalMessage);
        }

        if (originalMessage.contains("Duplicate entry")) {
            return parseDuplicateKeyUniqueConstraint(originalMessage);
        }

        log.info("Unhandled Exception: " + originalMessage);
        return "A data conflict occurred. The operation could not be completed. Please check your input and try again.";
    }

    private String parseUniqueConstraintViolation(String message) {
        Pattern tablePattern = Pattern.compile("object '(.+?)'");
        Pattern valuePattern = Pattern.compile("The duplicate key value is \\((.+?)\\)");
        Pattern columnPattern = Pattern.compile("column '(.+?)'");

        Matcher tableMatcher = tablePattern.matcher(message);
        Matcher valueMatcher = valuePattern.matcher(message);
        Matcher columnMatcher = columnPattern.matcher(message);

        if (tableMatcher.find() && valueMatcher.find()) {
            String tableName = tableMatcher.group(1).replace("dbo.", "").replace("_", " ");
            String duplicateValue = valueMatcher.group(1);
            String columnName = columnMatcher.group(1);
            return "A " + tableName + " with the " + columnName + " '" + duplicateValue + "' already exists. Please use a different value.";
        }

        return "A duplicate entry was detected. Please ensure all values are unique where required.";
    }

    private String parseNotNullConstraintViolation(String message) {
        Pattern columnPattern = Pattern.compile("column '(.+?)'");
        Matcher columnMatcher = columnPattern.matcher(message);

        if (columnMatcher.find()) {
            String columnName = columnMatcher.group(1).replace("_", " ");
            return "The " + columnName + " field cannot be empty. Please provide a value.";
        }

        return "A required field is missing. Please ensure all required fields are filled.";
    }

    private String parseForeignKeyConstraintViolation(String message) {
        return "The operation could not be completed due to a reference constraint. Please ensure all referenced data exists.";
    }

    private String parseDuplicateKeyUniqueConstraint(String message) {
        Pattern pattern = Pattern.compile("Duplicate entry '(.*?)' for key '(.*?)'");
        Matcher matcher = pattern.matcher(message);

        String duplicateEntry = null;
        String constraintName = null;

        if (matcher.find()) {
            duplicateEntry = matcher.group(1);
            constraintName = matcher.group(2);
        }

        return "A duplicate entry '" + duplicateEntry + "' was found. Please check your input and try again.";

    }


}

package com.backend.golvia.app.utilities;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public abstract class ResponseHelper {

    public static ResponseEntity<Map<String, Object>> success(Object data, String message, HttpStatus statusCode) {
        return buildResponse(statusCode.value(), message, data, null, statusCode);
    }

    public static ResponseEntity<Map<String, Object>> error(String message, HttpStatus statusCode) {
        return buildResponse(statusCode.value(), message, null, null, statusCode);
    }

    public static ResponseEntity<Map<String, Object>> created(String message) {
        return buildResponse(HttpStatus.CREATED.value(), message, null, null, HttpStatus.CREATED);
    }

    public static ResponseEntity<Map<String, Object>> updated(String message) {
        return buildResponse(HttpStatus.OK.value(), message, null, null, HttpStatus.OK);
    }

    public static ResponseEntity<Map<String, Object>> internalServerError(String message) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, null, null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity<Map<String, Object>> unauthorized(String message) {
        return buildResponse(HttpStatus.UNAUTHORIZED.value(), message, null, null, HttpStatus.UNAUTHORIZED);
    }

    public static ResponseEntity<Map<String, Object>> forbidden(String message) {
        return buildResponse(HttpStatus.FORBIDDEN.value(), message, null, null, HttpStatus.FORBIDDEN);
    }

    public static ResponseEntity<Map<String, Object>> notFound(String message) {
        return buildResponse(HttpStatus.NOT_FOUND.value(), message, null, null, HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity<Map<String, Object>> unprocessableEntity(String message) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), message, null, null, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private static ResponseEntity<Map<String, Object>> buildResponse(
            int status,
            String message,
            Object data,
            Object errors,
            HttpStatus statusCode
    ) {
        Date timestamp = new Date();

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", status);
        responseBody.put("message", Optional.ofNullable(message).orElse("No message available"));
        responseBody.put("data", Optional.ofNullable(data).orElse("No data available"));
        responseBody.put("timestamp", timestamp);
        responseBody.put("errors", errors);

        return ResponseEntity.status(statusCode)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseBody);
    }

    public static String implodeNestedArray(Map<String, Object> data, List<String> keys, String separator) {
        for (String key : keys) {
            if (data.containsKey(key) && data.get(key) instanceof List) {
                String joinedValue = StringUtils.collectionToDelimitedString((List<?>) data.get(key), separator);
                if (!joinedValue.isEmpty()) {
                    return joinedValue;
                }
            }
        }
        return "";
    }
}

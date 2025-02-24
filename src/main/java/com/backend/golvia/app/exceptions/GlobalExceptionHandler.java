//package com.backend.golvia.app.exceptions;
//
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.HttpMediaTypeNotAcceptableException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.context.request.WebRequest;
//
//import com.backend.golvia.app.models.response.ErrorResponse;
//
//import io.jsonwebtoken.ExpiredJwtException;
//
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.multipart.MaxUploadSizeExceededException;
//
//import java.sql.SQLIntegrityConstraintViolationException;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestControllerAdvice
//@Slf4j
//public class GlobalExceptionHandler {
//
//
//    // 1. Handle Resource Not Found Exception
//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
//        ErrorResponse errorResponse = new ErrorResponse(
//                HttpStatus.NOT_FOUND.value(),
//                ex.getMessage(),
//                LocalDateTime.now(),
//                request.getDescription(false)
//        );
//        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//    }
//
//    // 2. Handle Bad Request Exception
//    @ExceptionHandler(BadRequestException.class)
//    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex, WebRequest request) {
//        ErrorResponse errorResponse = new ErrorResponse(
//                HttpStatus.BAD_REQUEST.value(),
//                ex.getMessage(),
//                LocalDateTime.now(),
//                request.getDescription(false)
//        );
//        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//    }
//
//    // 3. Handle Validation Errors (e.g., MethodArgumentNotValidException)
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
//        Map<String, String> errors = new HashMap<>();
//        ex.getBindingResult().getAllErrors().forEach((error) -> {
//            String fieldName = ((FieldError) error).getField();
//            String errorMessage = error.getDefaultMessage();
//            errors.put(fieldName, errorMessage);
//        });
//
//        ErrorResponse errorResponse = new ErrorResponse(
//                HttpStatus.BAD_REQUEST.value(),
//                "Validation failed",
//                LocalDateTime.now(),
//                errors
//        );
//        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//    }
//
//    // 4. Handle Unauthorized Access (Security-related exceptions)
//    @ExceptionHandler(UnauthorizedException.class)
//    public ResponseEntity<Map<String, Object>> handleUnauthorizedException(
//            UnauthorizedException ex, WebRequest request) {
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("timestamp", LocalDateTime.now());
//        response.put("status", HttpStatus.UNAUTHORIZED.value());
//        response.put("error", "Unauthorized");
//        response.put("message", ex.getMessage());
//        response.put("path", request.getDescription(false).replace("uri=", ""));
//
//        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
//    }
//
//    // 5. Handle Forbidden Access
//    @ExceptionHandler(ForbiddenException.class)
//    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex, WebRequest request) {
//        ErrorResponse errorResponse = new ErrorResponse(
//                HttpStatus.FORBIDDEN.value(),
//                ex.getMessage(),
//                LocalDateTime.now(),
//                request.getDescription(false)
//        );
//        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
//    }
//
//    // 6. Handle Internal Server Error (General Exception)
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
//        ErrorResponse errorResponse = new ErrorResponse(
//                HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                "Internal server error",
//                LocalDateTime.now(),
//                null
//        );
//        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//    // 7. Handle IllegalArgumentException
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
//        ErrorResponse errorResponse = new ErrorResponse(
//                HttpStatus.BAD_REQUEST.value(),
//                ex.getMessage(),
//                LocalDateTime.now(),
//                request.getDescription(false)
//        );
//        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//    }
//
//    // 8. Handle EntityAlreadyExistsException (custom)
//    @ExceptionHandler(EntityAlreadyExistsException.class)
//    public ResponseEntity<ErrorResponse> handleEntityAlreadyExistsException(EntityAlreadyExistsException ex, WebRequest request) {
//        ErrorResponse errorResponse = new ErrorResponse(
//                HttpStatus.CONFLICT.value(),
//                ex.getMessage(),
//                LocalDateTime.now(),
//                request.getDescription(false)
//        );
//        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
//    }
//
//    // 9. Handle SQLIntegrityConstraintViolationException
//    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
//    public ResponseEntity<ErrorResponse> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex) {
//        ErrorResponse errorResponse = new ErrorResponse(
//                HttpStatus.CONFLICT.value(),
//                "Integrity constraint violation: " + ex.getMessage(),
//                LocalDateTime.now(),
//                null
//        );
//        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
//    }
//
//    // 10. Handle RuntimeException
////    @ExceptionHandler(RuntimeException.class)
////    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
////        ErrorResponse errorResponse = new ErrorResponse(
////                HttpStatus.BAD_REQUEST.value(),
////                "Details about the error",
////                LocalDateTime.now(),
////                request.getDescription(false)
////        );
////        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
////    }
//
//    // 11. Handle ExpiredJwtException
//    @ExceptionHandler(ExpiredJwtException.class)
//    public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException ex, WebRequest request) {
//        ErrorResponse errorResponse = new ErrorResponse(
//                HttpStatus.UNAUTHORIZED.value(),
//                "JWT token is expired",
//                LocalDateTime.now(),
//                request.getDescription(false)
//        );
//        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
//    }
//
//    @ExceptionHandler(MaxUploadSizeExceededException.class)
//    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException exc) {
//        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
//                .body("File size exceeds the maximum limit!");
//    }
//
//    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
//    public ResponseEntity<String> handleMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
//        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
//                .body("No acceptable representation for this request.");
//    }
//
//    @ExceptionHandler(CusException.class)
//    public final ResponseEntity<ErrorResponse> handleCustomizedErrors(CusException ex, WebRequest request) {
//        log.error(ex.getMessage(), ex);
//        log.error("Action taken from", request.getLocale().getCountry());
//        ErrorResponse errorResponse = new ErrorResponse(400,ex.getMessage(), LocalDateTime.now(), null);
//       // ErrorResponse errorResponse = new ErrorResponse(ex.(),ex.getMessage(), LocalDateTime.now(), ex.getCode());
//        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(400));
//    }
//
//
//}

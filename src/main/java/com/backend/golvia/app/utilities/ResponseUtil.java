package com.backend.golvia.app.utilities;

import com.backend.golvia.app.models.response.ApiResponse;
import com.backend.golvia.app.models.response.AuthResponse;

public class ResponseUtil {

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(200, message, data, null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Success", data, null);
    }

    public static <T> ApiResponse<T> error(String message, Object errors) {
        return new ApiResponse<>(400, message, null, errors);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(400, message, null, null);
    }
    
    
    public static <T> AuthResponse<T> auth_success(String token, Object data) {
        return new AuthResponse<>(201, token ,"Successfully logged in.",data);
    }
    
    
    public static <T> AuthResponse<T> auth_failure(String message) {
        return new AuthResponse<>(401, null, message,null);
    }
    
    
    public static <T> AuthResponse<T> auth_activate(String email) {
        return new AuthResponse<>(201, null, "An OTP has been sent to your email "+email,null);
    }
    
    public static <T> ApiResponse<T> register_success(T data) {
        return new ApiResponse<>(201, "An OTP has been sent to your email ", data,null);
    }
    
    
    public static <T> ApiResponse<T> bad_request(String message) {
        return new ApiResponse<>(400, message, null,null);
    }
    
    public static <T> ApiResponse<T> unprocessable_request(String message) {
    	 return new ApiResponse<>(422, message, null,null);
    }
    
    //
}


package com.backend.golvia.app.interceptors;

import com.backend.golvia.app.utilities.EncryptUtil;
import com.backend.golvia.app.utilities.JwtUtil;
import com.backend.golvia.app.utilities.ResponseHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Configuration
public class RequestInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestInterceptor.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        addCorsHeaders(response);

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpStatus.OK.value());
            return false;
        }

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.warn("Unauthorized access attempt: Missing or malformed Authorization header");
            sendErrorResponse(response, "Unauthorized: Missing or malformed Authorization header", HttpStatus.UNAUTHORIZED);
            return false;
        }

        String token = authorizationHeader.substring(7);

        if (!validateToken(token, response)) {
            logger.warn("Unauthorized access attempt: Invalid Bearer token");
            sendErrorResponse(response, "Unauthorized: Invalid Bearer token", HttpStatus.UNAUTHORIZED);
            return false;
        }

        return true;
    }

    private boolean validateToken(String token, HttpServletResponse response) {
        try {
            return jwtUtil.validateToken(token);
        } catch (Exception e) {
            return false;
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message, HttpStatus status) throws Exception {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(EncryptUtil.convertObjectToJson(ResponseHelper.unauthorized(message).getBody()));
    }

    private void addCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        response.setHeader("Access-Control-Max-Age", "3600");
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }
}

package com.backend.golvia.app.controllers.notification;

import com.backend.golvia.app.entities.User;
import com.backend.golvia.app.exceptions.UnauthorizedException;
import com.backend.golvia.app.models.response.ApiResponse;
import com.backend.golvia.app.repositories.auth.UserRepository;
import com.backend.golvia.app.services.notification.NotificationService;
import com.backend.golvia.app.utilities.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;


@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/settings")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("comment_notif")
    public ResponseEntity<ApiResponse<Void>> toggleCommentNotification(
            @RequestHeader("Action") String action,
            HttpServletRequest request) {
        if (!"comment_notif".equalsIgnoreCase(action)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(400, "Invalid Action Header", null));
        }
        try {
            User user = getUserFromRequest(request);
            ApiResponse<Void> response = notificationService.toggleCommentNotification(user.getEmail());
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (UnauthorizedException | EntityNotFoundException e) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>(404, e.getMessage(), null));
        }
    }
    // Toggle connection notification setting
    @PostMapping("connection_notif")
    public ResponseEntity<ApiResponse<Void>> toggleConnectionNotification(
            @RequestHeader("Action") String action,
            HttpServletRequest request) {
        // Validate the Action header
        if (!"connection_notif".equalsIgnoreCase(action)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(400, "Invalid Action Header", null));
        }
        try {
            // Retrieve the user from the request
            User user = getUserFromRequest(request);
            // Call the service method to toggle the connection notification setting
            ApiResponse<Void> response = notificationService.toggleConnectionNotification(user.getEmail());

            // Return the response
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (UnauthorizedException | EntityNotFoundException e) {
            // Return a 404 error if the user is not found or unauthorized
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>(404, e.getMessage(), null));
        }
    }

    // Toggle network notification setting
    @PostMapping("network_notif")
    public ResponseEntity<ApiResponse<Void>> toggleNetworkNotification(
            @RequestHeader("Action") String action,
            HttpServletRequest request) {
        // Validate the Action header
        if (!"network_notif".equalsIgnoreCase(action)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(400, "Invalid Action Header", null));
        }
        try {
            // Retrieve the user from the request
            User user = getUserFromRequest(request);
            // Call the service method to toggle the network notification setting
            ApiResponse<Void> response = notificationService.toggleNetworkNotification(user.getEmail());
            // Return the response
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (UnauthorizedException | EntityNotFoundException e) {
            // Return a 404 error if the user is not found or unauthorized
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>(404, e.getMessage(), null));
        }
    }

    // Toggle tags notification setting
    @PostMapping("tags_notif")
    public ResponseEntity<ApiResponse<Void>> toggleTagsNotification(
            @RequestHeader("Action") String action,
            HttpServletRequest request) {
        // Validate the Action header
        if (!"tags_notif".equalsIgnoreCase(action)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(400, "Invalid Action Header", null));
        }
        try {
            // Retrieve the user from the request
            User user = getUserFromRequest(request);

            // Call the service method to toggle the tags notification setting
            ApiResponse<Void> response = notificationService.toggleTagsNotification(user.getEmail());

            // Return the response
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (UnauthorizedException | EntityNotFoundException e) {
            // Return a 404 error if the user is not found or unauthorized
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>(404, e.getMessage(), null));
        }
    }

    private String extractEmailFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractUsername(token); // Assumes email is the username
    }

    public User getUserFromRequest(HttpServletRequest request) {
        String userEmail = extractEmailFromToken(request);
        if (userEmail == null) {
            throw new UnauthorizedException("Invalid or expired token");
        }

        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }

        return user;
    }
}
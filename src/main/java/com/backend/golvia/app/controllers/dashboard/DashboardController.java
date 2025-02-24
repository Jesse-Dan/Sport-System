package com.backend.golvia.app.controllers.dashboard;

import com.backend.golvia.app.channels.LogChannel;
import com.backend.golvia.app.entities.User;
import com.backend.golvia.app.models.UserInfo;
import com.backend.golvia.app.services.UserService;
import com.backend.golvia.app.services.dashboard.DashboardService;
import com.backend.golvia.app.utilities.JwtUtil;
import com.backend.golvia.app.utilities.ResponseHelper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/dashboard/")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public DashboardController(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @LogChannel
    @GetMapping("counts/dailyUsers")
    public ResponseEntity<Map<String, Object>> getDailyActiveUsers() {
        try {
            // Delegate to service layer
            return ResponseEntity.ok(dashboardService.getDailyActiveUsers());
        } catch (Exception e) {
            e.printStackTrace();

            // Construct an error response
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", "An error occurred while fetching daily active users.");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());

            // Return a 500 Internal Server Error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @LogChannel
    @GetMapping("counts/weeklyUsers")
    public ResponseEntity<Map<String, Object>> getWeeklyActiveUsers() {
        try {
            return ResponseEntity.ok(dashboardService.getWeeklyActiveUsers());
        } catch (Exception e) {
            e.printStackTrace();

            // Construct an error response
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", "An error occurred while fetching weekly active users.");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());

            // Return a 500 Internal Server Error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @LogChannel
    @GetMapping("counts/monthlyUsers")
    public ResponseEntity<Map<String, Object>> getMonthlyActiveUsers() {
        try {
            return ResponseEntity.ok(dashboardService.getMonthlyActiveUsers());
        } catch (Exception e) {
            e.printStackTrace();

            // Construct an error response
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", "An error occurred while fetching monthly active users.");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());

            // Return a 500 Internal Server Error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @LogChannel
    @GetMapping("counts/videos")
    public ResponseEntity<Map<String, Object>> getTotalUploadedVideos() {
        try {
            return ResponseEntity.ok(dashboardService.getTotalUploadedVideos());
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", "An error occurred while fetching total uploaded videos.");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @LogChannel
    @GetMapping("counts/likes")
    public ResponseEntity<Map<String, Object>> getTotalLikes() {
        try {
            return ResponseEntity.ok(dashboardService.getTotalLikes());
        } catch (Exception e) {
            e.printStackTrace();

            // Construct an error response
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", "An error occurred while fetching total likes.");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());

            // Return a 500 Internal Server Error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @LogChannel
    @GetMapping("all-users-details")
    public ResponseEntity<?> getAllUsers(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam Optional<Boolean> withMetaData
    ) {
        try {
            // Retrieve the current user (optional validation)
            User presentUser = jwtUtil.getUserFromToken(authorizationHeader);

            // Fetch all users from the database
            List<UserInfo<?>> allUsers = userService.getAllUsers(presentUser, withMetaData);

            return ResponseHelper.success(allUsers, "All user details retrieved successfully", HttpStatus.OK);

        } catch (Exception e) {
            return ResponseHelper.internalServerError(e.getMessage());
        }
    }

    @LogChannel
    @DeleteMapping("delete-user")
    public ResponseEntity<?> deleteUser(
            @Parameter(
                    description = "Request body containing email of the user to delete",
                    schema = @Schema(example = "{\"email\": \"user@example.com\"}")
            )
            @RequestBody Map<String, String> requestBody,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            // Validate JWT token
            User currentUser = jwtUtil.getUserFromToken(authorizationHeader);

            // Allow only the "support@gol-via.com" email to perform this action
            if (!"admin@gol-via.com".equalsIgnoreCase(currentUser.getEmail())) {
                return ResponseHelper.error("Unauthorized: Only an ADMIN can delete users", HttpStatus.FORBIDDEN);
            }

            // Extract email from request body
            String email = requestBody.get("email");
            if (email == null || email.isEmpty()) {
                return ResponseHelper.error("Email is required", HttpStatus.BAD_REQUEST);
            }

            // Call the service method to delete the user
            String responseMessage = userService.deleteUserByEmail(email);

            if (responseMessage.equals("User and associated data deleted successfully")) {
                return ResponseHelper.success(null, responseMessage, HttpStatus.OK);
            } else {
                return ResponseHelper.error(responseMessage, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return ResponseHelper.internalServerError(e.getMessage());
        }
    }
}


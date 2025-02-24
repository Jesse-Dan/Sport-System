package com.backend.golvia.app.controllers.activities;

import java.time.LocalDateTime;
import java.util.List;

import com.backend.golvia.app.utilities.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.golvia.app.channels.LogChannel;
import com.backend.golvia.app.dtos.activity_log.ActivityLogDTO;
import com.backend.golvia.app.services.activities.ActivityLogService;
import com.backend.golvia.app.utilities.ResponseHelper;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/activities")
public class ActivityLogController {

    @Autowired
    private ActivityLogService service;

    @Autowired
    private JwtUtil jwtUtil;

    @LogChannel
    @PostMapping("/log")
    public ResponseEntity<?> logActivity(
            @RequestBody ActivityLogDTO dto,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
            String email = jwtUtil.extractUsername(token);
            service.logActivity(email, dto);
            return ResponseHelper.success(null, "Activity logged successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseHelper.internalServerError("Failed to log activity");
        }
    }
    @LogChannel
    @GetMapping("/search")
    public ResponseEntity<?> getActivitiesByTypeAndDate(
            @RequestParam String activityType,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
            String email = jwtUtil.extractUsername(token);

            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<ActivityLogDTO> activities = service.getActivitiesByTypeAndDate(email, activityType, start, end);
            return ResponseHelper.success(activities, "Activities retrieved successfully", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseHelper.internalServerError("Failed to retrieve activities");
        }
    }
}

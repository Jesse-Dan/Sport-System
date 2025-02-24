package com.backend.golvia.app.controllers.activities;

import com.backend.golvia.app.dtos.activity_log.StatsDto;
import com.backend.golvia.app.entities.ActivityStats;
import com.backend.golvia.app.services.activities.ActivityStatsService;
import com.backend.golvia.app.utilities.JwtUtil;
import com.backend.golvia.app.utilities.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/activity-stats")
public class ActivityStatsController {

    @Autowired
    private ActivityStatsService activityStatsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> createActivityStat(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
            String email = jwtUtil.extractUsername(token);
            ActivityStats createdStat = activityStatsService.createActivityStats(email);
            return ResponseHelper.success(createdStat, "Activity stat created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseHelper.internalServerError(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getActivityStats(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
            String email = jwtUtil.extractUsername(token);

            Optional<ActivityStats> stats = activityStatsService.getAllActivityStats(email);

            return ResponseHelper.success(stats, "Activity stats retrieved successfully", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseHelper.internalServerError(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> updateActivityStat(
            @RequestBody StatsDto updatedActivityStats,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
            String email = jwtUtil.extractUsername(token);

            ActivityStats updatedStat = activityStatsService.updateActivityStats(email, updatedActivityStats);
            return ResponseHelper.success(updatedStat, "Activity stat updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseHelper.internalServerError(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteActivityStat(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
            String email = jwtUtil.extractUsername(token);

            activityStatsService.deleteActivityStats(email);
            return ResponseHelper.success(null, "Activity stat deleted successfully", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return ResponseHelper.internalServerError(e.getMessage());
        }
    }
}

package com.backend.golvia.app.services.activities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.golvia.app.dtos.activity_log.ActivityLogDTO;
import com.backend.golvia.app.entities.ActivityLog;
import com.backend.golvia.app.repositories.activities.ActivityLogRepository;

@Service
public class ActivityLogService {

    @Autowired
    private ActivityLogRepository repository;

    public ActivityLog logActivity(String email,ActivityLogDTO dto) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setActivityType(dto.getActivityType());
        activityLog.setActivityDescription(dto.getActivityDescription());
        activityLog.setUserEmail(email);
        activityLog.setTimestamp(dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now());
        return repository.save(activityLog);
    }

    public ActivityLog log(String email,String activity) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setActivityType("User Activity");
        activityLog.setActivityDescription(activity);
        activityLog.setUserEmail(email);
        activityLog.setTimestamp(LocalDateTime.now());
        return repository.save(activityLog);
    }

    public List<ActivityLogDTO> getActivitiesByTypeAndDate(String email, String activityType, LocalDateTime startDate, LocalDateTime endDate) {
        List<ActivityLog> activities = repository.findByUserEmailAndActivityTypeAndTimestampBetween(email, activityType, startDate, endDate);
        return activities.stream()
                .map(activity -> {
                    ActivityLogDTO dto = new ActivityLogDTO();
                    dto.setActivityType(activity.getActivityType());
                    dto.setActivityDescription(activity.getActivityDescription());
                    dto.setTimestamp(activity.getTimestamp());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}

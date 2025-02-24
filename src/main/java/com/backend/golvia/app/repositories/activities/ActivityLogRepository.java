package com.backend.golvia.app.repositories.activities;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.golvia.app.entities.ActivityLog;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByActivityTypeAndTimestampBetween(String activityType, LocalDateTime startDate, LocalDateTime endDate);

    List<ActivityLog> findByUserEmailAndActivityTypeAndTimestampBetween(
        String userEmail,
        String activityType,
        LocalDateTime startTime,
        LocalDateTime endTime
    );
}
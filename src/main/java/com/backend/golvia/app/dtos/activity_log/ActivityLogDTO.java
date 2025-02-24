package com.backend.golvia.app.dtos.activity_log;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityLogDTO {
    private String activityType;
    private String activityDescription;
    private LocalDateTime timestamp;
}
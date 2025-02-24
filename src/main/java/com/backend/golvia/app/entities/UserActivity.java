package com.backend.golvia.app.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_activities")
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_identifier")
    private String userIdentifier;

    @Column(name = "activity_type")
    private String activityType;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    // Constructors, Getters, Setters
    public UserActivity() {}

    public UserActivity(String userIdentifier, String activityType, LocalDateTime timestamp) {
        this.userIdentifier = userIdentifier;
        this.activityType = activityType;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

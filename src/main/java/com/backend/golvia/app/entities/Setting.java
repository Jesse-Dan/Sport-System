package com.backend.golvia.app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user_settings")
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150, unique = true)
    private String email;

    @Column(length = 150)
    private boolean has2FA = false;

    // Notification settings
    @Column(name = "comment_notification_enabled", nullable = false)
    private Boolean commentNotificationEnabled = true;

    @Column(name = "connection_notification_enabled", nullable = false)
    private Boolean connectionNotificationEnabled = true;

    @Column(name = "network_notification_enabled", nullable = false)
    private Boolean networkNotificationEnabled = true;

    @Column(name = "tags_notification_enabled", nullable = false)
    private Boolean tagsNotificationEnabled = true;

    // Timestamps
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    // Lifecycle hooks for setting timestamps
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }

    // Utility methods for checking notification statuses
    public boolean isCommentNotificationEnabled() {
        return commentNotificationEnabled != null && commentNotificationEnabled;
    }

    public boolean isConnectionNotificationEnabled() {
        return connectionNotificationEnabled != null && connectionNotificationEnabled;
    }

    public boolean isNetworkNotificationEnabled() {
        return networkNotificationEnabled != null && networkNotificationEnabled;
    }

    public boolean isTagsNotificationEnabled() {
        return tagsNotificationEnabled != null && tagsNotificationEnabled;
    }
}

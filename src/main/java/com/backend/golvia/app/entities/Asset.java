package com.backend.golvia.app.entities;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "profile_assets")
@Setter
@Getter
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = true)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "profile_reel_url")
    private String profileReelUrl;

    @Column(name = "cover_photo_url")
    private String coverPhotoUrl;

    @Column(name = "created_at", nullable = true, updatable = true)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

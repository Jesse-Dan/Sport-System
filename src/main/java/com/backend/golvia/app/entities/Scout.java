package com.backend.golvia.app.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "scouts")
public class Scout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String username;

    @Column(nullable = false, length = 150, unique = true)
    private String email;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "scouting_experience_years", nullable = false)
    private Integer scoutingExperienceYears;

    @ElementCollection
    @CollectionTable(name = "scouted_talents", joinColumns = @JoinColumn(name = "scout_id"))
    @Column(name = "talent_name", length = 255)
    private List<String> notableTalents;

    @ElementCollection
    @CollectionTable(name = "specializations", joinColumns = @JoinColumn(name = "scout_id"))
    @Column(name = "specialization", length = 50)
    private List<String> areasOfSpecialization;

    @ElementCollection
    @CollectionTable(name = "affiliated_organizations", joinColumns = @JoinColumn(name = "scout_id"))
    @Column(name = "organization_name", length = 255)
    private List<String> affiliatedOrganizations;

    @ElementCollection
    @CollectionTable(name = "scouting_regions", joinColumns = @JoinColumn(name = "scout_id"))
    @Column(name = "region_name", length = 100)
    private List<String> scoutingRegions;

    @ElementCollection
    @CollectionTable(name = "certifications", joinColumns = @JoinColumn(name = "scout_id"))
    @Column(name = "certification_name", length = 255)
    private List<String> certifications;


    @ElementCollection
    @CollectionTable(name = "regions_of_interest", joinColumns = @JoinColumn(name = "scout_id"))
    @Column(name = "region_name", length = 100)
    private List<String> regionsOfInterest;


    @ElementCollection
    @CollectionTable(name = "scouted_atlethes", joinColumns = @JoinColumn(name = "scout_id"))
    @Column(name = "atlethe_email", length = 255)
    private List<String> scoutedAtlethes;

    @Column(name = "notes_on_athletes", columnDefinition = "TEXT")
    private String notesOnAthletes;

    @Column(name = "position", columnDefinition = "TEXT")
    private String position;

    @Column(name = "phone_number", length = 225)
    private String phoneNumber;

    @Column(name = "age_group", length = 225)
    private String ageGroup;

    @Column(name = "history", length = 225)
    private String scoutingHistory;

    @ElementCollection
    @CollectionTable(name = "sports_of_interest", joinColumns = @JoinColumn(name = "scout_id"))
    @Column(name = "sport", length = 100)
    private List<String> sports;

    @Column(name = "social_media_links", columnDefinition = "TEXT")
    private String socialMediaLinks;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }
}

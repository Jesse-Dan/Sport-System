package com.backend.golvia.app.entities;

import com.backend.golvia.app.enums.MediaType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "challenges")
@Data
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @Column(name = "creator_email", nullable = false)
    @JsonIgnore
    private String creatorEmail;

    @Enumerated(EnumType.STRING)
    @Column(name="media_type")
    private MediaType mediaType;

    private String description;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<ChallengeEntry> challengeEntries;

    @ElementCollection
    @CollectionTable(name = "challenge_sponsors", joinColumns = @JoinColumn(name = "scout_id"))
    @Column(name = "challenge_sponsors", length = 255)
    private List<String> sponsors;


    public void addUserChallengeEntries(ChallengeEntry challengeEntries){
        this.challengeEntries.add(challengeEntries);
    }

    @Setter
    private boolean hasJoined;

    @Setter
    private boolean hasSubmitted;


    @Column(updatable = false)
    private LocalDateTime startDate;

    @Column(updatable = false)
    private LocalDateTime endDate;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dateCreated;

    @UpdateTimestamp
    private LocalDateTime dateUpdated;
}

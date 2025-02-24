package com.backend.golvia.app.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "challenges_entries")
@Data
public class ChallengeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private UserData user;

    @ManyToOne
    @JoinColumn(name = "challenge_id", nullable = true)
    @JsonBackReference
    @JsonIgnore
    private Challenge challenge;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "challenge_post_id", nullable = true)
    @JsonBackReference
    @JsonIgnore
    private Post challengePost;


    @Embedded
    private Impressions impressions;


    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dateCreated;

    @UpdateTimestamp
    private LocalDateTime dateUpdated;
}


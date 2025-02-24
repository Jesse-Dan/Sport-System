package com.backend.golvia.app.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "clubs")
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "club_name", nullable = false, unique = true, length = 100)
    private String clubName;

    @Column(nullable = false, length = 50)
    private String country;

    @Column(length = 50)
    private String city;

    @Column(name = "competition_level", length = 50)
    private String competitionLevel;

    @Column(name = "team_logo_url")
    private String teamLogoUrl;

    @Column(name = "contact_person_name", length = 100)
    private String contactPersonName;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "website", length = 100)
    private String website;

    @ElementCollection
    @CollectionTable(name = "club_social_links", joinColumns = @JoinColumn(name = "club_id"))
    private List<String> socialLinks;

    @OneToMany( targetEntity = Player.class)
    @JoinColumn(name = "player_id", nullable = true)
    private List<Player> players;


    @Column(name = "recruitment_areas", length = 100)
    private String recruitmentAreas;

    @Column(name = "player_type", length = 255)
    private String playerType;

    @ElementCollection
    @CollectionTable(name = "club_vacancies", joinColumns = @JoinColumn(name = "club_id"))
    private List<String> clubVacancies;

    @ElementCollection
    @CollectionTable(name = "club_achievements", joinColumns = @JoinColumn(name = "club_id"))
    private List<String> clubAchievements;
}


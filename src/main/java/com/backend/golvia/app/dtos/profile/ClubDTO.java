package com.backend.golvia.app.dtos.profile;

import com.backend.golvia.app.entities.Player;
import lombok.Data;

import java.util.List;

@Data
public class ClubDTO {
    private Long id;
    private String clubName;
    private String country;
    private String city;
    private String competitionLevel;
    private String teamLogoUrl;
    private String contactPersonName;
    private String contactEmail;
    private String contactPhone;
    private String website;
    private List<String> socialLinks;
    private List<Player> players;
    private String recruitmentAreas;
    private String playerType;
    private AssetDto asset;
    private List<String> clubVacancies;
    private List<String> clubAchievements;
}

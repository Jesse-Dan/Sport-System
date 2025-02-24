package com.backend.golvia.app.dtos.profile;

import com.backend.golvia.app.entities.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubUpdateDto {
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

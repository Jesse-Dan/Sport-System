package com.backend.golvia.app.dtos.profile;

import java.time.LocalDateTime;

import com.backend.golvia.app.entities.Athlete.Profession;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AthleteDto {

    private Long id;
    private String address;
    private String dateOfBirth;
    private Integer yearsOfExperience;
    private String height;
    private String weight;
    private String biography;
    private String currentClub;
    private String preferredPosition;
    private String preferredFoot;
    private String preferredClub;
    private String email;
    private AssetDto asset;
    private Profession profession;
}
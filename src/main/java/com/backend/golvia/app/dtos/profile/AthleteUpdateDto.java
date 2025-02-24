package com.backend.golvia.app.dtos.profile;

import com.backend.golvia.app.entities.Athlete.Profession;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AthleteUpdateDto {
    private String address;
    private String dateOfBirth;
    private int yearsOfExperience;
    private String height;
    private String weight;
    private String biography;
    private String currentClub;
    private String preferredPosition;
    private String preferredFoot;
    private String preferredClub;
    private Profession profession;
    private AssetDto asset;
}

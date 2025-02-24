package com.backend.golvia.app.dtos.profile;

import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class ScoutUpdateDto {
    private String username;
    private String city;
    private Integer scoutingExperienceYears;
    private List<String> notableTalents;
    private List<String> areasOfSpecialization;
    private List<String> affiliatedOrganizations;
    private List<String> scoutingRegions;
    private List<String> certifications;
    private List<String> regionsOfInterest;
    private List<String> scoutedAthletes;
    private List<String> sports;
    private String notesOnAthletes;
    private String position;
    private String ageGroup;
    private String scoutingHistory;
    private String phoneNumber;
    private String socialMediaLinks;
    private Boolean isActive;
    private AssetDto asset;
}

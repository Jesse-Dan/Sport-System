package com.backend.golvia.app.services.search;

import com.backend.golvia.app.dtos.interactions.GetUsersSingleUserDto;
import com.backend.golvia.app.entities.*;
import com.backend.golvia.app.enums.ProfileType;
import com.backend.golvia.app.repositories.auth.UserRepository;
import com.backend.golvia.app.repositories.profiles.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class SearchService {

    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final AthleteRepository athleteRepository;
    private final ClubRepository clubRepository;
    private final FanRepository fanRepository;
    private final ScoutRepository scoutRepository;

    public List<GetUsersSingleUserDto> search(String query) {
        List<GetUsersSingleUserDto> results = new ArrayList<>();

        results.addAll(
                userRepository.searchUsers(query).stream()
                        .map(user -> convertToDto(user, user.getProfileType()))
                        .toList()
        );

        results.addAll(
                athleteRepository.searchAthletes(query).stream()
                        .map(athlete -> fetchUserOrConvert(
                                athlete.getEmail(),
                                () -> convertToDtoFromAthlete(athlete)
                        ))
                        .toList()
        );

        results.addAll(
                clubRepository.searchClubs(query).stream()
                        .map(club -> fetchUserOrConvert(
                                club.getContactEmail(),
                                () -> convertToDtoFromClub(club)
                        ))
                        .toList()
        );

        results.addAll(
                fanRepository.searchFans(query).stream()
                        .map(fan -> fetchUserOrConvert(
                                fan.getEmail(),
                                () -> convertToDtoFromFan(fan)
                        ))
                        .toList()
        );

        results.addAll(
                scoutRepository.searchScouts(query).stream()
                        .map(scout -> fetchUserOrConvert(
                                scout.getEmail(),
                                () -> convertToDtoFromScout(scout)
                        ))
                        .toList()
        );

        return results;
    }

    private GetUsersSingleUserDto fetchUserOrConvert(String email, DtoSupplier fallbackDto) {
        return Optional.ofNullable(userRepository.findByEmail(email))
                .map(user -> convertToDto(user, user.getProfileType()))
                .orElseGet(fallbackDto::get);
    }


    private GetUsersSingleUserDto convertToDto(User user, ProfileType profileType) {
        GetUsersSingleUserDto dto = new GetUsersSingleUserDto();
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setCountry(user.getCountry());
        dto.setProfileType(profileType);
        dto.setSportType(user.getSportType());
        dto.setTeamName(user.getTeamName());
        dto.setActive(user.isActive());

        assetRepository.findByUserId(user.getId()).ifPresent(asset -> {
            dto.setProfilePictureUrl(asset.getProfilePictureUrl());
            dto.setProfileRealUrl(asset.getProfileReelUrl());
        });

        // Add metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("registrationType", user.getRegistationType());
        metadata.put("deleted", user.isDeleted());
        metadata.put("createdAt", user.getDateCreated());
        metadata.put("updatedAt", user.getDateUpdated());
        metadata.put("sportType", user.getSportType());
        metadata.put("teamName", user.getTeamName());

        dto.setMetaData(Map.of("profile", metadata));

        return dto;
    }

    private GetUsersSingleUserDto convertToDtoFromAthlete(Athlete athlete) {
        GetUsersSingleUserDto dto = new GetUsersSingleUserDto();
        dto.setEmail(athlete.getEmail());
        dto.setProfileType(ProfileType.ATHLETES);
        dto.setActive(true);

        // Add metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("address", athlete.getAddress());
        metadata.put("dateOfBirth", athlete.getDateOfBirth());
        metadata.put("yearsOfExperience", athlete.getYearsOfExperience());
        metadata.put("height", athlete.getHeight());
        metadata.put("weight", athlete.getWeight());
        metadata.put("biography", athlete.getBiography());
        metadata.put("currentClub", athlete.getCurrentClub());
        metadata.put("preferredPosition", athlete.getPreferredPosition());
        metadata.put("preferredFoot", athlete.getPreferredFoot());
        metadata.put("preferredClub", athlete.getPreferredClub());
        metadata.put("profession", athlete.getProfession());
        metadata.put("createdAt", athlete.getDateCreated());
        metadata.put("updatedAt", athlete.getDateUpdated());

        dto.setMetaData(Map.of("profile", metadata));

        return dto;
    }

    private GetUsersSingleUserDto convertToDtoFromClub(Club club) {
        GetUsersSingleUserDto dto = new GetUsersSingleUserDto();
        dto.setEmail(club.getContactEmail());
        dto.setProfileType(ProfileType.TEAM);
        dto.setActive(true);

        // Add metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("clubName", club.getClubName());
        metadata.put("country", club.getCountry());
        metadata.put("city", club.getCity());
        metadata.put("competitionLevel", club.getCompetitionLevel());
        metadata.put("teamLogoUrl", club.getTeamLogoUrl());
        metadata.put("contactPersonName", club.getContactPersonName());
        metadata.put("contactPhone", club.getContactPhone());
        metadata.put("website", club.getWebsite());
        metadata.put("socialLinks", club.getSocialLinks());
        metadata.put("recruitmentAreas", club.getRecruitmentAreas());
        metadata.put("playerType", club.getPlayerType());
        metadata.put("clubVacancies", club.getClubVacancies());
        metadata.put("clubAchievements", club.getClubAchievements());

        dto.setMetaData(Map.of("profile", metadata));

        return dto;
    }

    private GetUsersSingleUserDto convertToDtoFromFan(Fan fan) {
        GetUsersSingleUserDto dto = new GetUsersSingleUserDto();
        dto.setEmail(fan.getEmail());
        dto.setProfileType(ProfileType.FANBASE);
        dto.setActive(true);

        // Add metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("city", fan.getCity());
        metadata.put("favoriteSports", fan.getFavoriteSports());
        metadata.put("favoriteAthletes", fan.getFavoriteAthletes());
        metadata.put("notificationPreferences", fan.getNotificationPreferences());
        metadata.put("interactions", fan.getInteractions());
        metadata.put("purchasedItems", fan.getPurchasedItems());

        dto.setMetaData(Map.of("profile", metadata));

        return dto;
    }

    private GetUsersSingleUserDto convertToDtoFromScout(Scout scout) {
        GetUsersSingleUserDto dto = new GetUsersSingleUserDto();
        dto.setEmail(scout.getEmail());
        dto.setProfileType(ProfileType.SCOUT);
        dto.setActive(true);

        // Add metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("username", scout.getUsername());
        metadata.put("city", scout.getCity());
        metadata.put("scoutingExperienceYears", scout.getScoutingExperienceYears());
        metadata.put("notableTalents", scout.getNotableTalents());
        metadata.put("areasOfSpecialization", scout.getAreasOfSpecialization());
        metadata.put("affiliatedOrganizations", scout.getAffiliatedOrganizations());
        metadata.put("scoutingRegions", scout.getScoutingRegions());
        metadata.put("certifications", scout.getCertifications());
        metadata.put("notesOnAthletes", scout.getNotesOnAthletes());
        metadata.put("position", scout.getPosition());
        metadata.put("phoneNumber", scout.getPhoneNumber());
        metadata.put("ageGroup", scout.getAgeGroup());
        metadata.put("scoutingHistory", scout.getScoutingHistory());
        metadata.put("sports", scout.getSports());
        metadata.put("socialMediaLinks", scout.getSocialMediaLinks());
        metadata.put("createdAt", scout.getCreatedAt());
        metadata.put("updatedAt", scout.getUpdatedAt());

        dto.setMetaData(Map.of("profile", metadata));

        return dto;
    }

    @FunctionalInterface
    private interface DtoSupplier {
        GetUsersSingleUserDto get();
    }
}

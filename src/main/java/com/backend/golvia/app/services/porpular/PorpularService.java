package com.backend.golvia.app.services.porpular;

import com.backend.golvia.app.entities.*;
import com.backend.golvia.app.models.response.ApiResponse;
import com.backend.golvia.app.models.response.PopularResponse;
import com.backend.golvia.app.repositories.interactions.FollowerRepository;
import com.backend.golvia.app.repositories.post.PostRepository;
import com.backend.golvia.app.repositories.profiles.AssetRepository;
import com.backend.golvia.app.repositories.profiles.AthleteRepository;
import com.backend.golvia.app.repositories.profiles.ClubRepository;
import com.backend.golvia.app.repositories.profiles.ScoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PorpularService {

    private final ClubRepository clubRepository;
    private final FollowerRepository followerRepository;
    private final PostRepository postRepository;
    private final AthleteRepository athleteRepository;
    private final ScoutRepository scoutRepository;
    private final AssetRepository assetRepository;

    // Fetch top clubs
    public ApiResponse<List<PopularResponse>> getTopClubs() {
        return getTopEntities(
                clubRepository.findAll(),
                this::calculateClubRank,
                this::mapToPopularResponseForClub,
                "No teams available.",
                "Top teams successfully fetched."
        );
    }

    // Fetch top agents
    public ApiResponse<List<PopularResponse>> getTopAgents() {
        return getTopEntities(
                scoutRepository.findAll(),
                this::calculateAgentRank,
                this::mapToPopularResponseForAgent,
                "No agents available.",
                "Top agents successfully fetched."
        );
    }

    // Fetch top athletes
    public ApiResponse<List<PopularResponse>> getTopAthletes() {
        return getTopEntities(
                athleteRepository.findAll(),
                this::calculateAthleteRank,
                this::mapToPopularResponseForAthlete,
                "No athletes available.",
                "Top athletes successfully fetched."
        );
    }

    private <T> ApiResponse<List<PopularResponse>> getTopEntities(
            List<T> entities,
            java.util.function.ToDoubleFunction<T> rankFunction,
            java.util.function.Function<T, PopularResponse> mapper,
            String emptyMessage,
            String successMessage
    ) {
        if (entities.isEmpty()) {
            return new ApiResponse<>(201, emptyMessage, List.of(), LocalDateTime.now());
        }

        List<PopularResponse> topEntities = entities.stream()
                .sorted((a, b) -> Double.compare(rankFunction.applyAsDouble(b), rankFunction.applyAsDouble(a)))
                .limit(5)
                .map(mapper)
                .collect(Collectors.toList());

        return new ApiResponse<>(201, successMessage, topEntities, LocalDateTime.now());
    }

    private double calculateClubRank(Club club) {
        return 0.5 * getTotalFollowers(club.getContactEmail()) + 0.5 * getTotalPosts(club.getContactEmail());
    }

    private double calculateAgentRank(Scout scout) {
        return 0.5 * getTotalFollowersForAgent(scout) + 0.5 * scout.getScoutingExperienceYears();
    }

    private double calculateAthleteRank(Athlete athlete) {
        return 0.5 * athlete.getYearsOfExperience() + 0.5 * getTotalFollowersForAthlete(athlete);
    }

    private int getTotalFollowers(String email) {
        return email != null ? (int) followerRepository.countByToEmail(email) : 0;
    }

    private int getTotalPosts(String email) {
        return email != null ? (int) postRepository.countByUser_Email(email) : 0;
    }

    private int getTotalFollowersForAgent(Scout scout) {
        return scout.getScoutedAtlethes() != null ? scout.getScoutedAtlethes().size() : 0;
    }

    private int getTotalFollowersForAthlete(Athlete athlete) {
        return 0; // Replace with actual logic for calculating athlete's followers
    }

    private PopularResponse mapToPopularResponseForClub(Club club) {
        return PopularResponse.builder()
                .clubName(club.getClubName())
                .imageUrl(getProfilePicture(club.getId())) // Use the club's ID
                .followers(getTotalFollowers(club.getContactEmail()))
                .build();
    }

    private PopularResponse mapToPopularResponseForAgent(Scout scout) {
        return PopularResponse.builder()
                .clubName(scout.getUsername())
                .imageUrl(getProfilePicture(scout.getId())) // Use the scout's ID
                .followers(getTotalFollowersForAgent(scout))
                .build();
    }

    private PopularResponse mapToPopularResponseForAthlete(Athlete athlete) {
        return PopularResponse.builder()
                .clubName(athlete.getEmail()) // Adjust as needed for appropriate field
                .imageUrl(getProfilePicture(athlete.getId())) // Use the athlete's ID
                .followers(getTotalFollowersForAthlete(athlete))
                .build();
    }

    private String getProfilePicture(Long userId) {
        if (userId == null) {
            return null; // Return null if the user ID is not available
        }
        return assetRepository.findByUserId(userId)
                .map(Asset::getProfilePictureUrl) // Extract the profile picture URL if present
                .orElse(null); // Return null if no asset or profile picture exists
    }
}

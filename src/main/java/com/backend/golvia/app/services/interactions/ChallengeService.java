package com.backend.golvia.app.services.interactions;

import com.backend.golvia.app.dtos.activity_log.ActivityLogDTO;
import com.backend.golvia.app.dtos.challenges.CreateChallengePayloadDto;
import com.backend.golvia.app.dtos.interactions.LeaderboardData;
import com.backend.golvia.app.entities.*;

import com.backend.golvia.app.enums.InteractionStatus;
import com.backend.golvia.app.repositories.activities.UserActivityRepository;
import com.backend.golvia.app.repositories.auth.UserRepository;
import com.backend.golvia.app.repositories.interactions.ChallengeRepository;
import com.backend.golvia.app.repositories.interactions.ChallengeEntryRepository;
import com.backend.golvia.app.exceptions.ResourceNotFoundException;

import com.backend.golvia.app.repositories.interactions.ConnectionRepository;
import com.backend.golvia.app.repositories.post.PostRepository;
import com.backend.golvia.app.services.activities.ActivityLogService;
import com.backend.golvia.app.services.profile.asset.AssetService;
import com.backend.golvia.app.utilities.ResponsePagination;
import jakarta.transaction.Transactional;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeEntryRepository challengeEntryRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ConnectionRepository connectionRepository;
    private final AssetService assetService;
    private final UserActivityRepository userActivityRepository;
    private final ActivityLogService activityLogService;


    public void createChallenge(CreateChallengePayloadDto dto, String email) {
        Optional<Challenge> exChallenge = challengeRepository.findByChallengeType(dto.getTitle());

        if (exChallenge.isPresent()) {
            throw new IllegalArgumentException("Challenge with the same title already exists.");
        }

        Challenge challenge = new Challenge();
        challenge.setCreatorEmail(email);
        challenge.setSponsors(dto.getSponsors());
        challenge.setMediaType(dto.getMediaType());
        challenge.setStartDate(dto.getStartDate());
        challenge.setEndDate(dto.getEndDate());
        challenge.setTitle(dto.getTitle());
        challenge.setDescription(dto.getDescription());

        challengeRepository.save(challenge);

        activityLogService.log(email, "Created a new challenge with title: " + dto.getTitle());

        UserActivity userActivity = new UserActivity();
        userActivity.setUserIdentifier(email);
        userActivity.setTimestamp(LocalDateTime.now());
        userActivity.setActivityType("Created a new challenge with title: " + dto.getTitle());

        userActivityRepository.save(userActivity);
    }


    public void join(Long id, String email, String fN, String ln) throws ResourceNotFoundException, IllegalStateException {

        Optional<Challenge> exChallenge = challengeRepository.findById(id);

        if (exChallenge.isEmpty()) {
            throw new ResourceNotFoundException("Challenge not found.");
        }

        Challenge challenge = exChallenge.get();
        Optional<ChallengeEntry> existingEntry = challengeEntryRepository.findByUserEmailAndChallenge(email, challenge);

        if (existingEntry.isPresent()) {
            throw new IllegalStateException("User is already part of this challenge.");
        }

        ChallengeEntry challengeEntries = new ChallengeEntry();
        challengeEntries.setChallenge(challenge);

        UserData userData = new UserData();

        userData.setEmail(email);
        userData.setFirstName(fN);
        userData.setLastName(ln);

        challengeEntries.setUser(userData);
        challengeEntries.setChallenge(challenge);
        challengeEntryRepository.save(challengeEntries);

        UserActivity userActivity = new UserActivity();
        userActivity.setUserIdentifier(email);
        userActivity.setTimestamp(LocalDateTime.now());
        userActivity.setActivityType("Joined " + challenge.getTitle() + "challenge");

        userActivityRepository.save(userActivity);
    }

    public void withdraw(Long id, String email) throws Exception {

        Optional<Challenge> exChallenge = challengeRepository.findById(id);
        if (exChallenge.isEmpty()) {
            throw new Exception("Challenge not found.");
        }

        Challenge challenge = exChallenge.get();
        Optional<ChallengeEntry> userChallengeEntries = challengeEntryRepository.findByUserEmailAndChallenge(email, challenge);
        if (userChallengeEntries.isEmpty()) {
            throw new Exception("User is not part of this challenge.");
        }


        challengeEntryRepository.deleteById(userChallengeEntries.get().getId());

        UserActivity userActivity = new UserActivity();
        userActivity.setUserIdentifier(email);
        userActivity.setTimestamp(LocalDateTime.now());
        userActivity.setActivityType("Withdrew from " + challenge.getTitle() + "challenge");

        userActivityRepository.save(userActivity);
    }

    public void updateChallengeEntryPost(String email, Long challengeId, Long postId) throws Exception {
        if (postId == null || challengeId == null) {
            throw new IllegalArgumentException("Post ID and Challenge ID must be provided.");
        }

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new Exception("Challenge not found."));

        ChallengeEntry challengeEntry = challengeEntryRepository.findByChallengeIdAndUserEmail(challenge.getId(), email)
                .orElseThrow(() -> new Exception("Entry not found."));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new Exception("Post not found."));

        challengeEntry.setChallengePost(post);
    }

    public void deleteChallengeEntryPost(String email, Long postId) throws Exception {
        if (postId == null) {
            return;
        }

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new Exception("Post not found.");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new Exception("User not found.");
        }

        ChallengeEntry challengeEntry = challengeEntryRepository.findByEmailAndPost(user.getEmail(), post.getId()).orElse(null);

        if (challengeEntry != null) {
            challengeEntry.setChallengePost(null);
            challengeEntryRepository.save(challengeEntry);
        }
    }



    public ResponseEntity<ResponsePagination<Challenge>> getChallenges(String email, int page, int size) {


        List<Challenge> exChallenges = challengeRepository.findAll();

        int totalItems = exChallenges.size();

        for (Challenge challenge : exChallenges) {
            Optional<ChallengeEntry> entry = challengeEntryRepository.findByChallengeIdAndUserEmail(challenge.getId(), email);

            challenge.setHasJoined(entry.isPresent());

            entry.ifPresent(exEntry -> challenge.setHasSubmitted(exEntry.getChallengePost() != null));

        }

        return ResponsePagination.successWithPagination(
                exChallenges,
                page,
                size,
                totalItems,
                "Successfully retrieved challenges"
        );
    }


    public Challenge getChallengeById(String email, Long id) throws Exception {

        Optional<Challenge> exChallenges = challengeRepository.findById(id);

        if (exChallenges.isEmpty()) {
            throw new ResourceNotFoundException("Challenge not found.");
        }

        Challenge challenge = exChallenges.get();

        Optional<ChallengeEntry> entry = challengeEntryRepository.findByChallengeIdAndUserEmail(challenge.getId(), email);

        challenge.setHasJoined(entry.isPresent());

        entry.ifPresent(exEntry -> challenge.setHasSubmitted(exEntry.getChallengePost() != null));


        return challenge;
    }


    @Transactional
    public ResponseEntity<ResponsePagination<LeaderboardData>> leaderboard(Long id, int page, int size) {

        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found."));

        List<ChallengeEntry> challengeEntries = challengeEntryRepository.findByChallenge(challenge);

        List<LeaderboardData> leaderboardDataList = challengeEntries.stream().map(entry -> {
            User user = userRepository.findByEmail(entry.getUser().getEmail());
            Optional<Asset> asset = assetService.getAssetById(user);


            LeaderboardData leaderboardData = new LeaderboardData();
            LeaderboardData.UserData userData = new LeaderboardData.UserData();


            userData.setFirstName(user.getFirstName());
            userData.setLastName(user.getLastName());
            userData.setEmail(user.getEmail());

            asset.ifPresent(value -> userData.setProfileImageUrl(value.getProfilePictureUrl()));

            int connectionsCount = connectionRepository.countByStatusAndFromEmailOrToEmail(
                    InteractionStatus.ACCEPTED,
                    user.getEmail(),
                    user.getEmail()
            );
            userData.setConnections(connectionsCount);

            leaderboardData.setUser(userData);

            Post post = entry.getChallengePost();
            if (post != null) {
                leaderboardData.setLikes(post.getLikes() != null ? post.getLikes().size() : 0);
                leaderboardData.setCreatives(post.getCreatives() != null ? post.getCreatives().size() : 0);
            } else {
                leaderboardData.setLikes(0);
                leaderboardData.setCreatives(0);
            }

            return leaderboardData;
        }).toList();

        return  ResponsePagination.successWithPagination(
                leaderboardDataList,
                page,
                size,
                leaderboardDataList.size(),
                "Successfully retrieved leaderboard."
        );
    }
}

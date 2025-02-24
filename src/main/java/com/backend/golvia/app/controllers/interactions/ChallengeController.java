package com.backend.golvia.app.controllers.interactions;

import com.backend.golvia.app.dtos.challenges.CreateChallengePayloadDto;
import com.backend.golvia.app.dtos.interactions.LeaderboardData;
import com.backend.golvia.app.entities.Challenge;
import com.backend.golvia.app.entities.ChallengeEntry;
import com.backend.golvia.app.entities.User;
import com.backend.golvia.app.repositories.auth.UserRepository;
import com.backend.golvia.app.services.interactions.ChallengeService;
import com.backend.golvia.app.utilities.JwtUtil;
import com.backend.golvia.app.utilities.ResponseHelper;
import com.backend.golvia.app.utilities.ResponsePagination;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/challenges")
@AllArgsConstructor
public class ChallengeController {

    final private ChallengeService challengeService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createChallenge(
            @RequestBody CreateChallengePayloadDto dto,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
            String email = jwtUtil.extractUsername(token);

            challengeService.createChallenge(dto, email);
            return ResponseHelper.created("Challenge Created Successfully.");
        } catch (Exception e) {
            return ResponseHelper.unprocessableEntity(e.getMessage());
        }
    }

    @GetMapping("/{challengeId}/join")
    public ResponseEntity<?> joinChallenge(
            @PathVariable Long challengeId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
            String email = jwtUtil.extractUsername(token);
            User user = userRepository.getUserByEmail(email);

            challengeService.join(challengeId, user.getEmail(), user.getFirstName(), user.getLastName());
            return ResponseHelper.created("You Successfully joined the challenge.");
        } catch (Exception e) {
            return ResponseHelper.unprocessableEntity(e.getMessage());
        }
    }

    @GetMapping("/{challengeId}/withdraw")
    public ResponseEntity<?> withdrawChallenge(
            @PathVariable Long challengeId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
            String email = jwtUtil.extractUsername(token);

            challengeService.withdraw(challengeId, email);
            return ResponseHelper.created("You have successfully withdrawn the challenge.");
        } catch (Exception e) {
            return ResponseHelper.unprocessableEntity(e.getMessage());
        }
    }

    @GetMapping("/{challengeId}/leaderboard")
    public ResponseEntity<?> getLeaderboard(
            @PathVariable Long challengeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            return challengeService.leaderboard(challengeId, page, size);
        } catch (Exception e) {
            return ResponseHelper.unprocessableEntity(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getChallenges(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
            String email = jwtUtil.extractUsername(token);
            return challengeService.getChallenges(email, page, size);
        } catch (Exception e) {
            return ResponseHelper.unprocessableEntity(e.getMessage());
        }
    }

    @GetMapping("/{challengeId}")
    public ResponseEntity<?> getChallengeById(
            @PathVariable Long challengeId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
            String email = jwtUtil.extractUsername(token);
            Challenge challenge = challengeService.getChallengeById(email, challengeId);
            return ResponseHelper.success(challenge, "Challenge Fetched", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseHelper.unprocessableEntity(e.getMessage());
        }
    }

    @PostMapping("/{challengeId}/post/{postId}")
    public ResponseEntity<?> updateChallengeEntryPost(
            @PathVariable Long challengeId,
            @PathVariable Long postId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
            String email = jwtUtil.extractUsername(token);
            challengeService.updateChallengeEntryPost(email, challengeId, postId);
            return ResponseHelper.created("Challenge Entry Post Updated Successfully.");
        } catch (Exception e) {
            return ResponseHelper.unprocessableEntity(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/post")
    public ResponseEntity<?> deleteChallengeEntryPost(
            @PathVariable Long id, @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
            String email = jwtUtil.extractUsername(token);

            challengeService.deleteChallengeEntryPost(email, id);
            return ResponseHelper.created("Challenge Entry Post Removed Successfully.");
        } catch (Exception e) {
            return ResponseHelper.unprocessableEntity(e.getMessage());
        }
    }
}

package com.backend.golvia.app.services.interactions;

import com.backend.golvia.app.dtos.interactions.GetUsersSingleUserDto;
import com.backend.golvia.app.entities.*;
import com.backend.golvia.app.enums.InteractionStatus;
import com.backend.golvia.app.repositories.activities.UserActivityRepository;
import com.backend.golvia.app.repositories.auth.UserRepository;
import com.backend.golvia.app.repositories.interactions.FollowerRepository;
import com.backend.golvia.app.repositories.profiles.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class FollowerService {

    @Autowired
    private FollowerRepository followerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssetRepository assetRepository;

    private final UserActivityRepository userActivityRepository;

    public FollowerService(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    // Toggle Follow and Unfollow Another User
    public String toggleFollow(String fromEmail, String toEmail) {
        // Input validation
        if (fromEmail == null || fromEmail.isBlank()) {
            throw new IllegalArgumentException("From email is required");
        }
        if (toEmail == null || toEmail.isBlank()) {
            throw new IllegalArgumentException("To email is required");
        }

        Follower existingFollower = followerRepository.findByFromEmailAndToEmail(fromEmail, toEmail);



        if (existingFollower != null) {
            followerRepository.delete(existingFollower);
            // Record the action in user_activities
            UserActivity userActivity = new UserActivity();
            userActivity.setUserIdentifier(fromEmail);  // You can adjust this to store user IDs instead of emails
            userActivity.setTimestamp(LocalDateTime.now());
            userActivity.setActivityType("Unfollowed " + toEmail);

            // Save the activity
            userActivityRepository.save(userActivity);
            return "Unfollowed";

        } else {
            Follower newFollower = new Follower();
            newFollower.setFromEmail(fromEmail);
            newFollower.setToEmail(toEmail);
            newFollower.setStatus(InteractionStatus.ACCEPTED);
            followerRepository.save(newFollower);
            // Record the action in user_activities
            UserActivity userActivity = new UserActivity();
            userActivity.setUserIdentifier(fromEmail);  // You can adjust this to store user IDs instead of emails
            userActivity.setTimestamp(LocalDateTime.now());
            userActivity.setActivityType("Followed " + toEmail);
            userActivityRepository.save(userActivity);

            return "Followed";
        }
    }

    // Get followers of a specific user
    public List<GetUsersSingleUserDto> getFollowers(String fromEmail) {
        // Input validation
        if (fromEmail == null || fromEmail.isBlank()) {
            throw new IllegalArgumentException("From email is required");
        }

        List<Follower> followers = followerRepository.findByToEmailAndStatus(fromEmail, InteractionStatus.ACCEPTED.toString());

        List<GetUsersSingleUserDto> result = new ArrayList<>();

        followers.forEach(follower -> {
            User user = userRepository.findByEmail(follower.getFromEmail());

            if (user != null) {
                GetUsersSingleUserDto dto = new GetUsersSingleUserDto();
                dto.setEmail(user.getEmail());
                dto.setFirstName(user.getFirstName());
                dto.setLastName(user.getLastName());
                dto.setCountry(user.getCountry());
                dto.setProfileType(user.getProfileType());
                dto.setSportType(user.getSportType());
                dto.setTeamName(user.getTeamName());
                dto.setActive(user.isActive());

                Optional<Asset> asset = assetRepository.findByUserId(user.getId());

                asset.ifPresent(n -> {
                    dto.setProfilePictureUrl(n.getProfilePictureUrl());
                    dto.setProfileRealUrl(n.getProfileReelUrl());
                });

                result.add(dto);
            }
        });
        return result;
    }

    // Get users followed by a specific user
    public List<GetUsersSingleUserDto> getFollowing(String toEmail) {
        // Input validation
        if (toEmail == null || toEmail.isBlank()) {
            throw new IllegalArgumentException("To email is required");
        }

        List<Follower> followers = followerRepository.findByFromEmailAndStatus(toEmail, InteractionStatus.ACCEPTED.toString());

        return followers.stream()
                .map(follower -> {
                    User user = userRepository.findByEmail(follower.getToEmail());
                    if (user == null) {
                        throw new IllegalStateException(String.format("User not found for email: %s", follower.getToEmail()));
                    }
                    GetUsersSingleUserDto dto = new GetUsersSingleUserDto();
                    dto.setEmail(user.getEmail());
                    dto.setFirstName(user.getFirstName());
                    dto.setLastName(user.getLastName());
                    dto.setCountry(user.getCountry());
                    dto.setProfileType(user.getProfileType());
                    dto.setSportType(user.getSportType());
                    dto.setTeamName(user.getTeamName());
                    dto.setActive(user.isActive());

                    Optional<Asset> asset = assetRepository.findByUserId(user.getId());

                    asset.ifPresent(n -> {
                        dto.setProfilePictureUrl(n.getProfilePictureUrl());
                        dto.setProfileRealUrl(n.getProfileReelUrl());
                    });

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Implementation of email notification on follow (Stub example)
    public void notifyFollow(String followerEmail, String followeeEmail) {
        if (followerEmail == null || followerEmail.isBlank()) {
            throw new IllegalArgumentException("Follower email is required");
        }
        if (followeeEmail == null || followeeEmail.isBlank()) {
            throw new IllegalArgumentException("Followee email is required");
        }
        System.out.printf("Sending email notification to %s about being followed by %s%n", followeeEmail, followerEmail);
    }

    public List<GetUsersSingleUserDto> getUnFollowedUsers(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        List<User> unfollowedUsers = userRepository.findUnfollowedUsers(email);
        User mUser = userRepository.findByEmail(email);

        if (mUser == null) {
            throw new IllegalStateException(String.format("User not found for email: %s", email));
        }

        unfollowedUsers.remove(mUser);

        return unfollowedUsers.stream()
                .map(user -> {
                    GetUsersSingleUserDto dto = new GetUsersSingleUserDto();
                    dto.setEmail(user.getEmail());
                    dto.setFirstName(user.getFirstName());
                    dto.setLastName(user.getLastName());
                    dto.setCountry(user.getCountry());
                    dto.setProfileType(user.getProfileType());
                    dto.setSportType(user.getSportType());
                    dto.setTeamName(user.getTeamName());
                    dto.setActive(user.isActive());

                    Optional<Asset> asset = assetRepository.findByUserId(user.getId());

                    asset.ifPresent(n -> {
                        dto.setProfilePictureUrl(n.getProfilePictureUrl());
                        dto.setProfileRealUrl(n.getProfileReelUrl());
                    });

                    return dto;
                })
                .collect(Collectors.toList());
    }
}

package com.backend.golvia.app.services.interactions;

import com.backend.golvia.app.dtos.interactions.AcceptConnectionDto;
import com.backend.golvia.app.dtos.interactions.GetUsersSingleUserDto;
import com.backend.golvia.app.entities.Asset;
import com.backend.golvia.app.entities.Connection;
import com.backend.golvia.app.entities.User;
import com.backend.golvia.app.entities.UserActivity;
import com.backend.golvia.app.enums.InteractionStatus;
import com.backend.golvia.app.enums.ProfileType;
import com.backend.golvia.app.repositories.activities.UserActivityRepository;
import com.backend.golvia.app.repositories.auth.UserRepository;
import com.backend.golvia.app.repositories.interactions.ConnectionRepository;
import com.backend.golvia.app.repositories.profiles.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConnectionService {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssetRepository assetRepository;

    private final UserActivityRepository userActivityRepository;

    public ConnectionService(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    // Connect with another user
    public Connection sendConnectionRequest(String fromEmail, String toEmail, String message) throws Exception {

        Connection exConnection = connectionRepository.findByStatusAndFromEmailAndToEmail(
                InteractionStatus.PENDING.toString(), fromEmail, toEmail);

        Connection exAcConnection = connectionRepository.findByStatusAndFromEmailAndToEmail(
                InteractionStatus.ACCEPTED.toString(), fromEmail, toEmail);


        if (exConnection != null) {
            throw new Exception("Pending Connection Already Exist");
        }

        if(exAcConnection !=null){
            throw new Exception("Connection Already Exist");
        }

        Connection connection = new Connection();
        connection.setFromEmail(fromEmail);
        connection.setToEmail(toEmail);
        connection.setStatus(InteractionStatus.PENDING);
        connection.setReason(message);
        connection.setCreatedAt(LocalDateTime.now());
        // Record the action in user_activities
        UserActivity userActivity = new UserActivity();
        userActivity.setUserIdentifier(fromEmail);  // You can adjust this to store user IDs instead of emails
        userActivity.setTimestamp(LocalDateTime.now());
        userActivity.setActivityType("Send Connection to " + toEmail);
        userActivityRepository.save(userActivity);

        return connectionRepository.save(connection);
    }

    // Accept connection from a user
    public Connection acceptConnectionRequest(User user, AcceptConnectionDto dto) throws Exception {
        String fromEmail = dto.getEmail();
        String toEmail = user.getEmail();

        System.out.println("From: " + fromEmail);
        System.out.println("To: " + toEmail);

        if (fromEmail == null || fromEmail.isBlank()) {
            throw new IllegalArgumentException("From email is required");
        }
        if (toEmail == null || toEmail.isBlank()) {
            throw new IllegalArgumentException("To email is required");
        }

        Connection connection = connectionRepository.findByStatusAndFromEmailAndToEmail(
                InteractionStatus.PENDING.toString(), fromEmail, toEmail);

        if (connection == null) {
            throw new Exception(String.format("No pending connection found from '%s' to '%s'", fromEmail, toEmail));
        }

        connection.setStatus(InteractionStatus.ACCEPTED);
        connection.setAcceptedAt(LocalDateTime.now());

        // Record the action in user_activities
        UserActivity userActivity = new UserActivity();
        userActivity.setUserIdentifier(fromEmail);  // You can adjust this to store user IDs instead of emails
        userActivity.setTimestamp(LocalDateTime.now());
        userActivity.setActivityType("Accepted Connection " + toEmail);
        userActivityRepository.save(userActivity);

        return connectionRepository.save(connection);

    }


    // Get connection requests sent by a user
    public List<GetUsersSingleUserDto> getConnectionRequestsSent(String fromEmail) {

        List<Connection> connections = connectionRepository.findByFromEmailAndStatus(fromEmail, InteractionStatus.PENDING.toString());

        List<GetUsersSingleUserDto> result = new ArrayList<>();

        connections.forEach(connection -> {

            User user = userRepository.findByEmail(connection.getToEmail());

            if(user != null) {

                GetUsersSingleUserDto dto = new GetUsersSingleUserDto();
                dto.setEmail(connection.getToEmail());
                dto.setFirstName(user.getFirstName());
                dto.setLastName(user.getLastName());
                dto.setCountry(user.getCountry());
                dto.setProfileType(user.getProfileType());
                dto.setSportType(user.getSportType());
                dto.setTeamName(user.getTeamName());
                dto.setActive(user.isActive());

                Optional<Asset> asset = assetRepository.findByUserId(user.getId());

                if (asset.isPresent()) {
                    Asset n = asset.get();
                    dto.setProfilePictureUrl(n.getProfilePictureUrl());
                    dto.setProfileRealUrl(n.getProfileReelUrl());
                }
                result.add(dto);
            }
        });
      return result;
    }

    // Get connection requests received by a user
    public List<GetUsersSingleUserDto> getConnectionRequestsReceived(String toEmail) {
            List<Connection> connections = connectionRepository.findByToEmailAndStatus(toEmail, InteractionStatus.PENDING.toString());

            return connections.stream()
                    .map(connection -> {
                        User user = userRepository.findByEmail(connection.getFromEmail());
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

                        if (asset.isPresent()){
                            Asset n = asset.get();
                            dto.setProfilePictureUrl(n.getProfilePictureUrl());
                            dto.setProfileRealUrl(n.getProfileReelUrl());
                        }
                        return dto;
                    })
                    .collect(Collectors.toList());
        }

        // Remove connection or decline request
        public void removeConnection(String email) throws Exception {

            List<Connection> exConnections = connectionRepository.findByFromEmailOrToEmail(
                    email, email);

            if (exConnections.isEmpty()) {
                throw new Exception("Connection Doesn't Exist");
            }

            connectionRepository.deleteById(exConnections.get(0).getId());
        }

    public void notifyConnection(String fromEmail, String toEmail, String message) {
        // Send an email notification to `toEmail` that `fromEmail` wants to connect
        System.out.printf("Sending email notification to %s about connection request from %s with message: %s%n", toEmail, fromEmail, message);
    }

    public List<GetUsersSingleUserDto> getUnconnectedUsers(String email) {
        List<User> unconnectedUsers = userRepository.findUnconnectedUsers(email);

        User currentUser = userRepository.findByEmail(email);

        unconnectedUsers.remove(currentUser);

        return unconnectedUsers.stream()
                .filter(user -> user.getProfileType() != null && !user.getProfileType().equals(ProfileType.TEAM))
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

                    assetRepository.findByUserId(user.getId()).ifPresent(asset -> {
                        dto.setProfilePictureUrl(asset.getProfilePictureUrl());
                        dto.setProfileRealUrl(asset.getProfileReelUrl());
                    });

                    return dto;
                })
                .collect(Collectors.toList());

    }

    public List<GetUsersSingleUserDto> getNetwork(String email) {
        List<Connection> connections = connectionRepository.findByStatusAndFromEmailOrToEmail( InteractionStatus.ACCEPTED.toString(), email, email);

        List<GetUsersSingleUserDto> result = new ArrayList<>();

        connections.forEach(connection -> {
            String connectedEmail = connection.getFromEmail().equals(email) ? connection.getToEmail() : connection.getFromEmail();

            User user = userRepository.findByEmail(connectedEmail);

            if (user != null) {
                // Populate the DTO
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
                if (asset.isPresent()) {
                    Asset n = asset.get();
                    dto.setProfilePictureUrl(n.getProfilePictureUrl());
                    dto.setProfileRealUrl(n.getProfileReelUrl());
                }

                result.add(dto);
            }
        });

        return result;
    }
}

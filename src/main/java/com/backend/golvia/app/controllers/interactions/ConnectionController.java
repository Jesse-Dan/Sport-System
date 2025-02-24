package com.backend.golvia.app.controllers.interactions;

import com.backend.golvia.app.channels.LogChannel;
import com.backend.golvia.app.dtos.interactions.AcceptConnectionDto;
import com.backend.golvia.app.dtos.interactions.GetUsersSingleUserDto;
import com.backend.golvia.app.dtos.interactions.ConnectionDTO;
import com.backend.golvia.app.dtos.interactions.SendConnectionDto;
import com.backend.golvia.app.entities.Connection;
import com.backend.golvia.app.entities.User;
import com.backend.golvia.app.services.UserService;
import com.backend.golvia.app.services.interactions.ConnectionService;
import com.backend.golvia.app.utilities.JwtUtil;
import com.backend.golvia.app.utilities.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/connections")
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private UserService userService;

    @Autowired
    private final JwtUtil jwtUtil;

    public ConnectionController(UserService userService, JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    // Connect with another user
    @PostMapping("/send")
    public ResponseEntity<?> sendConnectionRequest(
            @RequestBody SendConnectionDto dto,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try{
            String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
            String email = jwtUtil.extractUsername(token);
            Connection connection = connectionService.sendConnectionRequest(email, dto.getEmail(), dto.getMessage());
            return ResponseHelper.success(toDTO(connection), "Connection Sent", HttpStatus.OK);
        }   catch (Exception e) {
        return ResponseHelper.unprocessableEntity(e.getMessage());
    }

}

    // Accept a connection request
    @PostMapping("/accept")
    public ResponseEntity<?> acceptConnectionRequest(
            @RequestBody AcceptConnectionDto dto,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try{
            User user = jwtUtil.getUserFromToken(authorizationHeader);
            Connection connection = connectionService.acceptConnectionRequest(user, dto);

            return ResponseHelper.success(toDTO(connection), "Connection Accepted", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseHelper.unprocessableEntity(e.getMessage());
        }
    }

    // Get sent connection requests
    @GetMapping("/sent")
    public ResponseEntity<?> getSentRequests(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
        String email = jwtUtil.extractUsername(token);
        return ResponseHelper.success(connectionService.getConnectionRequestsSent(email).stream()
                .toList(),"Sent Connection Fetched", HttpStatus.OK);
    }

    // Get received connection requests
    @GetMapping("/received")
    public ResponseEntity<?> getReceivedRequests(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
        String email = jwtUtil.extractUsername(token);
        return ResponseHelper.success(connectionService.getConnectionRequestsReceived(email).stream()
                .toList(),"Connection Request Fetched",HttpStatus.OK);
    }

    // Remove a connection
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeConnection(@RequestParam String email) {
        try{
            connectionService.removeConnection(email);
            return ResponseHelper.success(null, "Connection Deleted Successfully", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseHelper.internalServerError(e.getMessage());
        }
    }

    // DTO conversion method
    private ConnectionDTO toDTO(Connection connection) {
        ConnectionDTO dto = new ConnectionDTO();
        dto.setId(connection.getId());
        dto.setStatus(connection.getStatus());
        dto.setFromEmail(connection.getFromEmail());
        dto.setToEmail(connection.getToEmail());
        dto.setReason(connection.getReason());
        dto.setCreatedAt(connection.getCreatedAt());
        dto.setAcceptedAt(connection.getAcceptedAt());
        return dto;
    }

    @LogChannel
    @GetMapping("unconnected-users")
    public ResponseEntity<?> getUsers(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            User user = jwtUtil.getUserFromToken(authorizationHeader);
            List<GetUsersSingleUserDto> userInfo = connectionService.getUnconnectedUsers(user.getEmail());
            return ResponseHelper.success(userInfo, "UnConnected Users retrieved successfully", HttpStatus.OK);

        } catch (RuntimeException e) {
            return ResponseHelper.internalServerError(e.getMessage());
        } catch (Exception e) {
            return ResponseHelper.internalServerError(e.getMessage());
        }

    }

    @LogChannel
    @GetMapping("networks")
    public ResponseEntity<?> network(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            User user = jwtUtil.getUserFromToken(authorizationHeader);
            List<GetUsersSingleUserDto> userInfo = connectionService.getNetwork(user.getEmail());
            return ResponseHelper.success(userInfo, "Network fetched successfully", HttpStatus.OK);

        } catch (RuntimeException e) {
            return ResponseHelper.unauthorized("Invalid or expired token");
        } catch (Exception e) {
            return ResponseHelper.internalServerError(e.getMessage());
        }

    }

}

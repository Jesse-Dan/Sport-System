package com.backend.golvia.app.controllers.interactions;

import com.backend.golvia.app.channels.LogChannel;
import com.backend.golvia.app.dtos.interactions.GetUsersSingleUserDto;
import com.backend.golvia.app.dtos.interactions.FollowDTO;
import com.backend.golvia.app.entities.Follower;
import com.backend.golvia.app.entities.User;
import com.backend.golvia.app.services.interactions.FollowerService;
import com.backend.golvia.app.utilities.JwtUtil;
import com.backend.golvia.app.utilities.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follows")
public class FollowerController {

    @Autowired
    private FollowerService followerService;

    @Autowired
    private final JwtUtil jwtUtil;

    public FollowerController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // Toggle follow/unfollow a user
    @PostMapping("/toggle")
    public ResponseEntity toggleFollow(
            @RequestParam String followerEmail,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
        String email = jwtUtil.extractUsername(token);

        String res = followerService.toggleFollow(email,followerEmail);
        return ResponseHelper.success(null,res,HttpStatus.ACCEPTED);
    }

    // Get followers of a user
    @GetMapping("/followers")
    public ResponseEntity<?> getFollowers(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
        String email = jwtUtil.extractUsername(token);

        return ResponseHelper.success(followerService.getFollowers(email).stream()
                .toList(),"Follower Fetched",HttpStatus.OK);
    }

    // Get users followed by a user
    @GetMapping("/following")
    public ResponseEntity<?> getFollowing(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
        String email = jwtUtil.extractUsername(token);

        return ResponseHelper.success( followerService.getFollowing(email).stream()
                .toList(),"Following Users List Fetched", HttpStatus.ACCEPTED);
    }

    // DTO conversion method
    private FollowDTO toDTO(Follower follow) {
        FollowDTO dto = new FollowDTO();
        dto.setId(follow.getId());
        dto.setStatus(follow.getStatus());
        dto.setFromEmail(follow.getFromEmail());
        dto.setToEmail(follow.getToEmail());
        dto.setCreatedAt(follow.getCreatedAt());
        return dto;
    }

    @LogChannel
    @GetMapping("unfollowed-users")
    public ResponseEntity<?> getUsers(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            User user = jwtUtil.getUserFromToken(authorizationHeader);
            List<GetUsersSingleUserDto> userInfo = followerService.getUnFollowedUsers(user.getEmail());
            return ResponseHelper.success(userInfo, "Unfollowed Users retrieved successfully", HttpStatus.OK);

        } catch (RuntimeException e) {
            return ResponseHelper.unauthorized("Invalid or expired token");
        } catch (Exception e) {
            return ResponseHelper.internalServerError(e.getMessage());
        }

    }
}

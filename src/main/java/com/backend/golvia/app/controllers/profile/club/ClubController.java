package com.backend.golvia.app.controllers.profile.club;

import java.util.List;
import java.util.Optional;

import com.backend.golvia.app.dtos.profile.ClubUpdateDto;
import com.backend.golvia.app.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.golvia.app.channels.LogChannel;
import com.backend.golvia.app.dtos.profile.ClubDTO;
import com.backend.golvia.app.services.profile.club.ClubService;
import com.backend.golvia.app.utilities.JwtUtil;
import com.backend.golvia.app.utilities.ResponseHelper;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/clubs")
public class ClubController {

    @Autowired
    private ClubService clubService;

    @Autowired
    private JwtUtil jwtUtil;

    @LogChannel
    @GetMapping
    public ResponseEntity<?> getAllClubs() {
        try{
            List<ClubDTO> clubs = clubService.findAllClubs();
            return ResponseHelper.success(clubs, "Clubs retrieved successfully", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseHelper.unprocessableEntity(e.getMessage());
        }
    }

//    @LogChannel
//    @PostMapping
//    public ResponseEntity<?> createClub(@RequestBody ClubDTO clubDto,
//            @RequestHeader("Authorization") String authorizationHeader) {
//        try {
//            System.out.println("Create Club Called ");
//
//            String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
//            String email = jwtUtil.extractUsername(token);
//            System.out.println("Retrieving athlete with EMAIL: " + email);
//            System.out.println("Retrieving athlete with TOKEN: " + token);
//
//            clubDto.setContactEmail(email);
//            ClubDTO createdAthlete = clubService.saveClub(clubDto);
//            return ResponseHelper.success(createdAthlete, "Club created successfully", HttpStatus.CREATED);
//        } catch (Exception e) {
//            return ResponseHelper.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//    }

    @LogChannel
    @PatchMapping
    public ResponseEntity<?> updateClub(@RequestBody ClubUpdateDto clubUpdateDto,
            @RequestHeader("Authorization") String authorizationHeader) {
        try{
            User user = jwtUtil.getUserFromToken(authorizationHeader);
            Optional<ClubDTO> updatedClub = clubService.updateClub(user.getEmail(), clubUpdateDto);
            return updatedClub.map(c -> ResponseHelper.success(c, "Club updated successfully", HttpStatus.OK))
                    .orElse(ResponseHelper.notFound("Club with specified ID not found"));
        } catch (Exception e) {
            return ResponseHelper.unprocessableEntity(e.getMessage());
        }
    }

    @LogChannel
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClub(@PathVariable Long id) {
        try{
            if (clubService.existsById(id)) {
                clubService.deleteClub(id);
                return ResponseHelper.success(null, "Club deleted successfully", HttpStatus.NO_CONTENT);
            } else {
                return ResponseHelper.notFound("Club with specified ID not found");
            }
        } catch (Exception e) {
            return ResponseHelper.unprocessableEntity(e.getMessage());
        }
    }

    @LogChannel
    @GetMapping("/search")
    public ResponseEntity<?> findClubsByCriteria(
            @RequestParam String country,
            @RequestParam String level,
            @RequestParam String recruitmentArea, @RequestParam String city,
            @RequestParam String name, @RequestParam String website) {
        try{
            List<ClubDTO> clubs = clubService.findClubsByCriteria(country, level, recruitmentArea, city,
                    name, website);
            return ResponseHelper.success(clubs, "Clubs by criteria retrieved successfully", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseHelper.unprocessableEntity(e.getMessage());
        }
    }
}

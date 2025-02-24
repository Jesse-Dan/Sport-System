package com.backend.golvia.app.controllers.profile.athlete;

import java.util.List;
import java.util.Optional;

import com.backend.golvia.app.channels.LogChannel;
import com.backend.golvia.app.dtos.profile.AthleteUpdateDto;
import com.backend.golvia.app.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.golvia.app.dtos.profile.AthleteDto;
import com.backend.golvia.app.services.profile.athlete.AthleteService;
import com.backend.golvia.app.utilities.JwtUtil;
import com.backend.golvia.app.utilities.ResponseHelper;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/athletes")
public class AthleteController {

    @Autowired
    private final AthleteService athleteService;

    @Autowired
    private final JwtUtil jwtUtil;

    public AthleteController(AthleteService athleteService, JwtUtil jwtUtil) {
        this.athleteService = athleteService;
        this.jwtUtil = jwtUtil;
    }

//    @LogChannel
//    @PostMapping
//    public ResponseEntity<?> createAthlete(@RequestBody AthleteDto athleteDto,
//            @RequestHeader("Authorization") String authorizationHeader) {
//        try {
//            System.out.println("Create Athlete Called ");
//
//            String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
//            String email = jwtUtil.extractUsername(token);
//            System.out.println("Retrieving athlete with EMAIL: " + email);
//            System.out.println("Retrieving athlete with TOKEN: " + token);
//
//            athleteDto.setEmail(email);
//            AthleteDto createdAthlete = athleteService.createAthlete(athleteDto);
//            return ResponseHelper.success(createdAthlete, "Athlete created successfully", HttpStatus.CREATED);
//        } catch (Exception e) {
//            return ResponseHelper.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @LogChannel
    @GetMapping
    public ResponseEntity<?> getAthleteById(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            User user = jwtUtil.getUserFromToken(authorizationHeader);
            Optional<AthleteDto> athlete = athleteService.getAthleteById(user.getEmail());
            return athlete
                    .map(value -> ResponseHelper.success(value, "Athlete retrieved successfully", HttpStatus.OK))
                    .orElseGet(() -> ResponseHelper.notFound("Athlete not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving athlete: " + e.getMessage());
        }
    }

    @LogChannel
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAthlete(@PathVariable Long id) {
        try {
            athleteService.deleteAthlete(id);
            return ResponseHelper.updated("Athlete deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting athlete: " + e.getMessage());
        }
    }

    @LogChannel
    @GetMapping("/query")
    public ResponseEntity<?> getAthletes(
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "years", required = false) Integer years) {
        try {
            List<AthleteDto> athletes = athleteService.getAthletesByCountryAndExperience(country, years);
            return ResponseHelper.success(athletes, "Athletes retrieved successfully", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving athletes: " + e.getMessage());
        }
    }

    @LogChannel
    @PatchMapping
    public ResponseEntity<?> updateAthletes(@RequestBody AthleteUpdateDto athleteDto,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            System.out.println("Updating Athlete");
            User user = jwtUtil.getUserFromToken(authorizationHeader);
            System.out.println("User email: " + user.getEmail());
            Optional<AthleteUpdateDto> updateAthlete = athleteService.updateAthlete(user.getEmail(), athleteDto);
            return ResponseHelper.success(updateAthlete, "Athlete updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseHelper.internalServerError("Error updating athlete: " + e.getMessage());
        }
    }
}

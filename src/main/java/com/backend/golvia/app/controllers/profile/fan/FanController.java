package com.backend.golvia.app.controllers.profile.fan;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.backend.golvia.app.dtos.profile.FanUpdateDto;
import com.backend.golvia.app.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.golvia.app.channels.LogChannel;
import com.backend.golvia.app.dtos.profile.FanDTO;
import com.backend.golvia.app.entities.Fan;
import com.backend.golvia.app.services.profile.fan.FanService;
import com.backend.golvia.app.utilities.JwtUtil;
import com.backend.golvia.app.utilities.ResponseHelper;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/fans")
public class FanController {

    @Autowired
    private FanService fanService;

    @Autowired
    private JwtUtil jwtUtil;

    // Create a new Fan
//    @LogChannel
//    @PostMapping
//    public ResponseEntity<Map<String, Object>> createFan(@RequestBody FanDTO fanDTO,
//            @RequestHeader("Authorization") String authorizationHeader) {
//        String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
//        String email = jwtUtil.extractUsername(token);
//        fanDTO.setEmail(email);
//        Fan createdFan = fanService.createFan(fanDTO);
//        return ResponseHelper.success(createdFan, "Fan created successfully", HttpStatus.CREATED);
//    }

    // Get a Fan by ID
    @LogChannel
    @GetMapping
    public ResponseEntity<Map<String, Object>> getFanById(@PathVariable Long id) {
        try{
            Optional<Fan> fan = fanService.getFanById(id);
            return fan.map(value -> ResponseHelper.success(value, "Fan retrieved successfully", HttpStatus.OK))
                    .orElseGet(() -> ResponseHelper.notFound("Fan not found"));
        } catch (Exception e) {
            return ResponseHelper.unprocessableEntity(e.getMessage());
        }
    }

    // Update an existing Fan
    @LogChannel
    @PatchMapping
    public ResponseEntity<Map<String, Object>> updateFan(@RequestBody FanUpdateDto fanDTO,
            @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        try{
            User user = jwtUtil.getUserFromToken(authorizationHeader);
            Fan updatedFan = fanService.updateFan(user.getEmail(), fanDTO);
            return ResponseHelper.success(updatedFan, "Fan updated successfully", HttpStatus.OK);

    } catch (Exception e) {
        return ResponseHelper.unprocessableEntity(e.getMessage());
    }}

    // Delete a Fan
    @LogChannel
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteFan(@PathVariable Long id) {
        try{
            fanService.deleteFan(id);
            return ResponseHelper.updated("Fan deleted successfully");
        } catch (Exception e) {
            return ResponseHelper.unprocessableEntity(e.getMessage());
        }
    }

    // Unified search for Fans by various criteria
    @LogChannel
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> findFansByCriteria(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String favoriteSport,
            @RequestParam(required = false) String favoriteAthlete,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String notificationPreference,
            @RequestParam(required = false) String purchasedItem,
            @RequestParam(defaultValue = "5") int limit) {

        try{
            List<Fan> fans = fanService.findFansByCriteria(country, favoriteSport, favoriteAthlete, username, city,
                    notificationPreference, purchasedItem, limit);
            return ResponseHelper.success(fans, "Fans retrieved successfully", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseHelper.unprocessableEntity(e.getMessage());
        }
    }
}
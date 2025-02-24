package com.backend.golvia.app.controllers.profile.scout;

import java.util.List;
import java.util.Map;

import com.backend.golvia.app.dtos.profile.ScoutUpdateDto;
import com.backend.golvia.app.entities.Athlete;
import com.backend.golvia.app.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.golvia.app.channels.LogChannel;
import com.backend.golvia.app.dtos.profile.ScoutDTO;
import com.backend.golvia.app.entities.Scout;
import com.backend.golvia.app.services.profile.scout.ScoutService;
import com.backend.golvia.app.utilities.JwtUtil;
import com.backend.golvia.app.utilities.ResponseHelper;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/scouts")
public class ScoutController {

    @Autowired
    private ScoutService scoutService;

    @Autowired
    private JwtUtil jwtUtil;

    @LogChannel
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchScouts(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String organization,
            @RequestParam(required = false) Integer years,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String certification,
            @RequestParam(required = false) String talent) {

        try{
            List<Scout> scouts = scoutService.findScoutsByCriteria(country, specialization, organization, years, region,
                    certification, talent);
            return ResponseHelper.success(scouts, "Scouts retrieved successfully", HttpStatus.OK);
        }   catch (Exception e) {
        return ResponseHelper.unprocessableEntity(e.getMessage());
    } }

    @LogChannel
    @GetMapping("all")
    public ResponseEntity<Map<String, Object>> getAllScouts() {
        try{
            List<ScoutDTO> scouts = scoutService.findAll();
            return ResponseHelper.success(scouts, "All scouts retrieved successfully", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseHelper.unprocessableEntity(e.getMessage());
        }    }

    @LogChannel
    @GetMapping("athlete")
    public ResponseEntity<Map<String, Object>> getScoutedAthletes(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try{
            User user = jwtUtil.getUserFromToken(authorizationHeader);
            List<Athlete> scouts = scoutService.getScoutedAthletes(user.getEmail());
            return ResponseHelper.success(scouts, "All scouts athletes retrieved successfully", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseHelper.unprocessableEntity(e.getMessage());
        }
    }

    @LogChannel
    @GetMapping
    public ResponseEntity<Map<String, Object>> getScoutById(
            @RequestHeader("Authorization") String authorizationHeader
    )  {
        try{
            User user = jwtUtil.getUserFromToken(authorizationHeader);
            ScoutDTO scout = scoutService.findByEmail(user.getEmail());
            if (scout != null) {
                return ResponseHelper.success(scout, "Scout retrieved successfully", HttpStatus.OK);
            } else {
                return ResponseHelper.notFound("Scout not found");
            }
        } catch (Exception e) {
            return ResponseHelper.unprocessableEntity(e.getMessage());
        }
    }


   @LogChannel
   @PostMapping
   public ResponseEntity<Map<String, Object>> createScout(
           @RequestBody ScoutDTO scoutDTO,
           @RequestHeader("Authorization") String authorizationHeader) {
       try {
           System.out.println("Create Scout Called ");

           User user = jwtUtil.getUserFromToken(authorizationHeader);
           scoutDTO.setEmail(user.getEmail());
           ScoutDTO newScout = scoutService.create(scoutDTO);
           return ResponseHelper.success(newScout, "Scout created successfully", HttpStatus.CREATED);
       } catch (Exception e) {
           return ResponseHelper.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
       }
   }



    @LogChannel
    @PatchMapping
    public ResponseEntity<Map<String, Object>> updateScout(@RequestBody ScoutUpdateDto scoutDTO,
            @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        try{
            User user = jwtUtil.getUserFromToken(authorizationHeader);
            ScoutDTO updatedScout = scoutService.update(user.getEmail(), scoutDTO);
            if (updatedScout != null) {
                return ResponseHelper.success(updatedScout, "Scout updated successfully", HttpStatus.OK);
            } else {
                return ResponseHelper.notFound("Scout not found");
            }
        } catch (Exception e) {
            return ResponseHelper.unprocessableEntity(e.getMessage());
        }
    }

    @LogChannel
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteScout(@PathVariable Long id) {
        try{
            boolean deleted = scoutService.delete(id);
            if (deleted) {
                return ResponseHelper.success(null, "Scout deleted successfully", HttpStatus.OK);
            } else {
                return ResponseHelper.notFound("Scout not found");
            }
        } catch (Exception e) {
            return ResponseHelper.unprocessableEntity(e.getMessage());
        }
    }

}
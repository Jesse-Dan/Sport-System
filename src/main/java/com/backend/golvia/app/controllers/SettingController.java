package com.backend.golvia.app.controllers;


import com.backend.golvia.app.dtos.settings.UpdateSettingsDto;
import com.backend.golvia.app.entities.Setting;
import com.backend.golvia.app.entities.User;
import com.backend.golvia.app.services.settings.SettingsService;
import com.backend.golvia.app.utilities.JwtUtil;
import com.backend.golvia.app.utilities.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/settings")
public class SettingController {

    @Autowired
    private final JwtUtil jwtUtil;

    @Autowired
    private final SettingsService settingsService;


    public SettingController(JwtUtil jwtUtil, SettingsService settingsService) {
        this.jwtUtil = jwtUtil;
        this.settingsService = settingsService;
    }

    @PatchMapping
    public ResponseEntity<?> updateSettings(
            @RequestBody UpdateSettingsDto dto,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            User user = jwtUtil.getUserFromToken(authorizationHeader);
            Setting setting = settingsService.updateUserSetting(user, dto);

            return ResponseHelper.success(setting,"Settings Updated", HttpStatus.OK);

        } catch (Exception e) {
            return ResponseHelper.unprocessableEntity(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getSettings(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            User user = jwtUtil.getUserFromToken(authorizationHeader);
            Setting setting = settingsService.getUserSetting(user);

            return ResponseHelper.success(setting,"User Settings Fetched", HttpStatus.OK);

        } catch (Exception e) {
            return ResponseHelper.unprocessableEntity(e.getMessage());
        }
    }
}

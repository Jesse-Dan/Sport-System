package com.backend.golvia.app.controllers;


import com.backend.golvia.app.channels.LogChannel;
import com.backend.golvia.app.dtos.GoogleUserDto;
import com.backend.golvia.app.dtos.GoogleUserDtoRes;
import com.backend.golvia.app.enums.GoogleAuthType;
import com.backend.golvia.app.models.UserInfo;
import com.backend.golvia.app.models.response.AuthResponse;
import com.backend.golvia.app.repositories.auth.UserRepository;
import com.backend.golvia.app.services.GoogleUserService;
import com.backend.golvia.app.services.UserService;
import com.backend.golvia.app.utilities.JwtUtil;
import com.backend.golvia.app.utilities.ResponseHelper;
import com.backend.golvia.app.utilities.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/google-auth")
public class GoogleUserController {

    @Autowired
    private GoogleUserService googleUserService;

    @LogChannel
    @PostMapping
    public ResponseEntity<?> defaultMethod(
            @RequestBody GoogleUserDto dto,
            @RequestHeader("x-google-auth-type") GoogleAuthType signUpType
            ){
        try{

            GoogleUserDtoRes res = switch (signUpType) {
                case SIGN_IN -> googleUserService.handleSignIn(dto);
                case SIGN_UP -> googleUserService.handleSignUp(dto);
            };

            AuthResponse<UserInfo> authResponse = new AuthResponse<UserInfo>(
                    200,
                    res.getToken() ,
                    "Google Authentication Successful",
                    res.getUserInfo()
            );

            return ResponseEntity.status(201).body(authResponse);


        } catch (Exception e) {
            return ResponseHelper.forbidden(e.getMessage());
        }

    }
}

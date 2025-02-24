package com.backend.golvia.app.services;

import com.backend.golvia.app.dtos.GoogleUserDto;
import com.backend.golvia.app.dtos.GoogleUserDtoRes;
import com.backend.golvia.app.dtos.profile.AssetDto;
import com.backend.golvia.app.entities.User;
import com.backend.golvia.app.entities.UserActivity;
import com.backend.golvia.app.enums.RegistrationType;
import com.backend.golvia.app.exceptions.CustomException;
import com.backend.golvia.app.models.UserInfo;
import com.backend.golvia.app.repositories.activities.UserActivityRepository;
import com.backend.golvia.app.repositories.auth.UserRepository;
import com.backend.golvia.app.services.profile.asset.AssetService;
import com.backend.golvia.app.services.settings.SettingsService;
import com.backend.golvia.app.utilities.JwtUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class GoogleUserService {

    private final UserRepository userRepository;
    private final SettingsService settingsService;
    private final UserService userService;
    private final AssetService assetService;
    private final JwtUtil jwtUtil;
    private final UserActivityRepository userActivityRepository;


    public GoogleUserService(UserRepository userRepository, SettingsService settingsService, UserService userService, AssetService assetService, JwtUtil jwtUtil, UserActivityRepository userActivityRepository) {
        this.userRepository = userRepository;
        this.settingsService = settingsService;
        this.userService = userService;
        this.assetService = assetService;
        this.jwtUtil = jwtUtil;
        this.userActivityRepository = userActivityRepository;
    }

    public GoogleUserDtoRes handleSignUp(GoogleUserDto dto) throws Exception {
        User user = userRepository.findByEmail(dto.getEmail());
        if (user == null) {
            user = createNewUser(dto);
            user.setRegistationType(RegistrationType.GOOGLE);
            userRepository.save(user);
            settingsService.initUserSetting(user);
        }

        UserInfo<?> userInfo = userService.getUserByEmail(user,dto.getEmail(), Optional.of(false));

        if (userInfo.getAsset() == null) {
            createAsset(user, dto);
        }

        GoogleUserDtoRes googleUserDtoRes = new GoogleUserDtoRes();
        googleUserDtoRes.setUserInfo(userInfo);
        googleUserDtoRes.setToken(jwtUtil.generateToken(userInfo.getUser().getEmail()));

        return googleUserDtoRes;
    }

    public GoogleUserDtoRes handleSignIn(GoogleUserDto dto) throws Exception {
        User user = null;
        try{
            user = userRepository.findByEmail(dto.getEmail());
        }catch (Exception ex){
            ex.printStackTrace();
            throw new CustomException("Error occured during findByEmail" + ex.getMessage(), ex);
        }

        if (user == null) {
            throw new Exception("User not found. Please sign up first.");
        }

        UserInfo<?> userInfo = userService.getUserByEmail(user,dto.getEmail(), Optional.of(false));

        if (userInfo.getAsset() == null) {
            createAsset(user, dto);
        }

        GoogleUserDtoRes googleUserDtoRes = new GoogleUserDtoRes();
        googleUserDtoRes.setUserInfo(userInfo);
        googleUserDtoRes.setToken(jwtUtil.generateToken(userInfo.getUser().getEmail()));

        return googleUserDtoRes;
    }


    private User createNewUser(GoogleUserDto dto) {
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getGiven_name());
        user.setLastName(dto.getFamily_name());
        user.setCountry(null);
        user.setActive(dto.isEmail_verified());
        user.setProfileType(null);
        user.setPassword(null);
        user.setDateCreated(now);
        user.setDateUpdated(now);

        //Create UserActivity entry for the comment creation
        UserActivity userActivity = new UserActivity();
        userActivity.setUserIdentifier(user.getEmail()); // or use user.getId() if you want to store the user ID
        userActivity.setActivityType("User Created");
        userActivity.setTimestamp(LocalDateTime.now()); // Set current timestamp
        // Save the UserActivity in the user_activity table
        userActivityRepository.save(userActivity);
        return user;
    }

    private void createAsset(User user, GoogleUserDto dto) {
        AssetDto assetDto = new AssetDto();
        assetDto.setProfilePictureUrl(dto.getPicture());

        assetService.createAsset(user, assetDto);
    }
}

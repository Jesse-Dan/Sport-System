package com.backend.golvia.app.controllers;

import com.backend.golvia.app.dtos.profile.*;
import com.backend.golvia.app.entities.Fan;
import com.backend.golvia.app.entities.User;
import com.backend.golvia.app.enums.ProfileType;
import com.backend.golvia.app.models.UserInfo;
import com.backend.golvia.app.repositories.auth.UserRepository;
import com.backend.golvia.app.services.profile.asset.AssetService;
import com.backend.golvia.app.services.profile.athlete.AthleteService;
import com.backend.golvia.app.services.profile.club.ClubService;
import com.backend.golvia.app.services.profile.fan.FanService;
import com.backend.golvia.app.services.profile.scout.ScoutService;
import com.backend.golvia.app.utilities.JwtUtil;
import com.backend.golvia.app.utilities.ResponseHelper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.golvia.app.channels.LogChannel;
import com.backend.golvia.app.models.request.UpdateProfileTypeRequest;

import com.backend.golvia.app.services.UserService;
import com.backend.golvia.app.utilities.ResponseUtil;

import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AthleteService athleteService;

	@Autowired
	private ScoutService scoutService;

	@Autowired
	private ClubService clubService;

	@Autowired
	private FanService fanService;

	@Autowired
	private AssetService assetService;


	@LogChannel
	@RequestMapping
	public String gateway_message() {

		return "This is the Backend service for Golvia .";
	}

	//
	// @PostMapping("/getProfile")
	// public ResponseEntity<UserInfo> subscriberProfile(@RequestBody User user) {
	// UserInfo singleUser = userService.getUserByEmail(user.getEmail());
	// return ResponseEntity.ok(singleUser);
	// }

	@LogChannel
	@PostMapping("/updateProfileType")
	public ResponseEntity<?> updateProfileType(@RequestBody UpdateProfileTypeRequest req) {

		try{
			if (req == null) {

				return ResponseEntity.status(400).body(ResponseUtil.bad_request("bad request"));
			}

			boolean response = userService.updateProfileType(req);

			if (response) {

				return ResponseEntity.status(200).body(ResponseUtil.success(null));
			} else {

				return ResponseEntity.status(422).body(ResponseUtil.unprocessable_request("unprocessable request"));

			}
		} catch (Exception e) {
			return ResponseHelper.unprocessableEntity(e.getMessage());
		}
	}

	@LogChannel
	@PostMapping("/create-profile")
	public ResponseEntity<?> createProfile(
			@RequestBody  Map<String, Object> profileDto,
			@RequestHeader("Authorization") String authorizationHeader
	) {
			String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
			String email = jwtUtil.extractUsername(token);
		try{
			Optional<User> optionalUser =
                    Optional.ofNullable(userRepository.findByEmail(email));
			if(optionalUser.isPresent()){
				User user = optionalUser.get();
				return determineProfileToBeCreated(user.getProfileType(),profileDto,user);
			}else{
				return ResponseHelper.notFound("User not found");
			}
		} catch (Exception e) {
			return ResponseHelper.unprocessableEntity(e.getMessage());
		}
	}


	private ResponseEntity<?> determineProfileToBeCreated(
			ProfileType profileType,
			Map<String, Object> profileDto,
			User user
	) throws Exception {

		Map<String, Object> assetMap = (Map<String, Object>) profileDto.get("asset");
		AssetDto newAsset = new AssetDto();
		newAsset.setProfilePictureUrl((String) assetMap.get("profilePictureUrl"));
		newAsset.setProfileReelUrl((String) assetMap.get("profileReelUrl"));
		newAsset.setCoverPhotoUrl((String) assetMap.get("coverPhotoUrl"));

		// Create Asset
		assetService.createAsset(user, newAsset);

		/// Continue Flow
		switch (profileType) {
			case ATHLETES:
				AthleteDto athleteDto = convertToDto(profileDto, AthleteDto.class);
				athleteDto.setEmail(user.getEmail());
				AthleteDto createdAthlete = athleteService.createAthlete(athleteDto);
				return ResponseHelper.created("Athlete created");

			case SCOUT:
				ScoutDTO scoutDTO = convertToDto(profileDto, ScoutDTO.class);
				scoutDTO.setEmail(user.getEmail());
				ScoutDTO createdScout = scoutService.create(scoutDTO);
				return ResponseHelper.created("Scout created");

			case TEAM:
				ClubDTO clubDto = convertToDto(profileDto, ClubDTO.class);
				clubDto.setContactEmail(user.getEmail());
				ClubDTO createdClub = clubService.saveClub(clubDto);
				return ResponseHelper.created("Club created");

			case FANBASE:
				FanDTO fanDTO = convertToDto(profileDto, FanDTO.class);
				fanDTO.setEmail(user.getEmail());
				Fan createdFan = fanService.createFan(fanDTO);
				return ResponseHelper.created("Fan created");

			default:
				return ResponseHelper.unprocessableEntity("Profile type not supported");
		}
	}

	private <T> T convertToDto(Object obj, Class<T> clazz) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper.convertValue(obj, clazz);
	}

	@LogChannel
	@GetMapping("user-details")
	public ResponseEntity<?> getUserProfile(
			@RequestParam Optional<String> email,
			@RequestParam Optional<Boolean> withMetaData,
			@RequestHeader("Authorization") String authorizationHeader
	) {
		try {
			User user = jwtUtil.getUserFromToken(authorizationHeader);


			if (email.isPresent()){
				UserInfo<?> userInfo = userService.getUserByEmail(user, email.get(), withMetaData);
				return ResponseHelper.success(userInfo, "User details retrieved successfully", HttpStatus.OK);
			}

			UserInfo<?> userInfo = userService.getUserByEmail(user, user.getEmail(), withMetaData);
			return ResponseHelper.success(userInfo, "User details retrieved successfully", HttpStatus.OK);

		} catch (Exception e) {
			return ResponseHelper.internalServerError(e.getMessage());
		}
	}
}
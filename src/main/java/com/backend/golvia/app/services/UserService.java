package com.backend.golvia.app.services;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.backend.golvia.app.services.post.PostService;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import com.backend.golvia.app.enums.InteractionStatus;
import com.backend.golvia.app.enums.RegistrationType;
import com.backend.golvia.app.models.response.AuthResponse;
import com.backend.golvia.app.repositories.interactions.ConnectionRepository;
import com.backend.golvia.app.repositories.interactions.FollowerRepository;
import com.backend.golvia.app.repositories.post.CommentRepository;
import com.backend.golvia.app.repositories.post.LikeRepository;
import com.backend.golvia.app.repositories.post.PostRepository;
import com.backend.golvia.app.services.activities.ActivityStatsService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.backend.golvia.app.entities.*;
import com.backend.golvia.app.exceptions.*;
import com.backend.golvia.app.models.UserInfo;
import com.backend.golvia.app.models.request.*;
import com.backend.golvia.app.repositories.auth.*;
import com.backend.golvia.app.repositories.profiles.*;
import com.backend.golvia.app.services.settings.SettingsService;
import com.backend.golvia.app.utilities.*;



@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	private final AssetRepository assetRepository;

	private final AthleteRepository athleteRepository;

	private final ScoutRepository scoutRepository;

	private final ClubRepository teamRepository;

	private final FanRepository fanRepository;

	private final ConfirmationTokenRepository confirmationTokenRepository;

	private final OtpService otpService;

	private final EmailService emailService;

	private final SettingsService settingsService;

	private  final EmailUtil emailUtil;

	private  final JwtUtil jwtUtil;

	private final ConnectionRepository connectionRepository;

	private final FollowerRepository followerRepository;

	private final ActivityStatsService activityStatsService;

	private final PostRepository postRepository;

	private final LikeRepository likeRepository;

	private final CommentRepository commentRepository;

	private final PostService postService;

	private final AssetRepository assetsRepository;
	private final SettingsRepository settingsRepository;



//	public List<UserInfo<?>> getAllUsers(
//			User presentUser,
//			Optional<Boolean> withMetaData) throws Exception {
//
//		// Fetch all users from the database
//		List<User> usersList = userRepository.findAll();
//
//		// Check if the result contains users
//		if (!usersList.isEmpty()) {
//			List<UserInfo<?>> userDetailsList = new ArrayList<>();
//
//			// Build detailed user information
//			for (User user : usersList) {
//				userDetailsList.add(buildUserInfo(user, presentUser, withMetaData));
//			}
//
//			return userDetailsList;
//		} else {
//			// Return an empty list if no users are found
//			return Collections.emptyList();
//		}
//	}
//
//
//	// Helper method to build UserInfo for a single user
//	private UserInfo<?> buildUserInfo(User user, User presentUser, Optional<Boolean> withMetaData) {
//		UserInfo<Object> userInfo = new UserInfo<>();
//		userInfo.setUser(user);
//
//		// Fetch user's asset
//		Optional<Asset> asset = assetRepository.findByUserId(user.getId());
//		userInfo.setAsset(asset.orElse(null));
//
//		// Prepare metadata
//		Map<String, Object> metadata = new HashMap<>();
//		if (withMetaData.isPresent()) {
//			metadata.put("connectionsCount", setUserConnections(user));
//			metadata.put("followersCount", setUserFollowers(user));
//
//			if (!user.getEmail().equals(presentUser.getEmail())) {
//				metadata.put("isFollowing", getIsFollowing(user, presentUser.getEmail()));
//				metadata.put("isConnected", getConnection(user, presentUser.getEmail()));
//			}
//		} else {
//			metadata.put("message", "Pass withMetaData as true to view Metadata.");
//		}
//
//		userInfo.setMetadata(metadata);
//
//		// Fetch user profile based on profile type
//		if (user.getProfileType() != null) {
//			switch (user.getProfileType()) {
//				case ATHLETES:
//					userInfo.setProfile(athleteRepository.findByEmail(user.getEmail()));
//					break;
//				case SCOUT:
//					userInfo.setProfile(scoutRepository.findByEmail(user.getEmail()));
//					break;
//				case TEAM:
//					userInfo.setProfile(teamRepository.findByContactEmail(user.getEmail()));
//					break;
//				case FANBASE:
//					userInfo.setProfile(fanRepository.findByEmail(user.getEmail()));
//					break;
//				default:
//					userInfo.setProfile(null);
//					break;
//			}
//		} else {
//			userInfo.setProfile(null);
//		}
//
//		// Register profile views for all users (except the current logged-in user)
//		if (!presentUser.getEmail().equals(user.getEmail())) {
//			activityStatsService.registerProfileViews(user.getEmail());
//		}
//
//		return userInfo;
//	}
	
	public List<UserInfo<?>> getAllUsers(User presentUser, Optional<Boolean> withMetaData) throws Exception {
	    try {
	        List<User> usersList = userRepository.findAll();

	        if (usersList.isEmpty()) {
	            return Collections.emptyList();
	        }

	        boolean includeMetaData = withMetaData.orElse(false);
	        return usersList.stream()
	                .map(user -> safeBuildUserInfo(user, presentUser, includeMetaData))
	                .collect(Collectors.toList());
	    } catch (Exception e) {
	        // Log and rethrow or handle exception
	    	System.out.println(e);
	        throw new Exception("An error occurred while fetching users.", e);
	    }
	}

	private UserInfo<?> safeBuildUserInfo(User user, User presentUser, boolean includeMetaData) {
	    try {
	        return buildUserInfo(user, presentUser, includeMetaData);
	    } catch (Exception e) {
	        // Log and return partial or default user info
	    	System.out.println(e);
	        UserInfo<Object> errorUserInfo = new UserInfo<>();
	        errorUserInfo.setUser(user);
	        errorUserInfo.setMetadata(Collections.singletonMap("error", "Failed to fetch user info."));
	        return errorUserInfo;
	    }
	}

	private UserInfo<?> buildUserInfo(User user, User presentUser, boolean includeMetaData) throws Exception {
	    UserInfo<Object> userInfo = new UserInfo<>();
	    userInfo.setUser(user);

	    // Set asset with error handling
	    try {
	        assetRepository.findByUserId(user.getId())
	                .ifPresent(userInfo::setAsset);
	    } catch (Exception e) {
	    	System.out.println(e);
	    }

	    // Add metadata if requested
	    if (includeMetaData) {
	        userInfo.setMetadata(buildMetadata(user, presentUser));
	    }

	    // Set profile with error handling
	    try {
	        userInfo.setProfile(fetchUserProfile(user));
	    } catch (Exception e) {
	    	System.out.println(e);
	    }

	    // Register profile view for other users
	    try {
	        if (!presentUser.getEmail().equals(user.getEmail())) {
	            activityStatsService.registerProfileViews(user.getEmail());
	        }
	    } catch (Exception e) {
	    	System.out.println(e);
	    }

	    return userInfo;
	}

	private Map<String, Object> buildMetadata(User user, User presentUser) {
	    try {
	        Map<String, Object> metadata = new HashMap<>();
	        metadata.put("connectionsCount", setUserConnections(user));
	        metadata.put("followersCount", setUserFollowers(user));

	        if (!user.getEmail().equals(presentUser.getEmail())) {
	            metadata.put("isFollowing", getIsFollowing(user, presentUser.getEmail()));
	            metadata.put("isConnected", getConnection(user, presentUser.getEmail()));
	        }

	        return metadata;
	    } catch (Exception e) {
	    	System.out.println("Error building metadata for user: {}"+e.getMessage());
	        return Collections.singletonMap("error", "Failed to fetch metadata.");
	    }
	}

	private Object fetchUserProfile(User user) {
	    try {
	        if (user.getProfileType() == null) {
	            return null;
	        }

	        switch (user.getProfileType()) {
	            case ATHLETES:
	                return athleteRepository.findByEmail(user.getEmail());
	            case SCOUT:
	                return scoutRepository.findByEmail(user.getEmail());
	            case TEAM:
	                return teamRepository.findByContactEmail(user.getEmail());
	            case FANBASE:
	                return fanRepository.findByEmail(user.getEmail());
	            default:
	                return null;
	        }
	    } catch (Exception e) {
	        System.out.println("Error fetching profile for user: {}"+e.getMessage());
	        return null;
	    }
	}



	public UserInfo<?> getUserByEmail(User presentUser,String referencedUserEmail, Optional<Boolean> withMetaData) throws Exception {

		User user = userRepository.findByEmail(referencedUserEmail);

		if(user == null){
			throw new Exception("User Not Found");
		}

		Optional<Asset> asset = assetRepository.findByUserId(user.getId());

		UserInfo<Object> userInfo = new UserInfo<>();

		userInfo.setUser(user);

		userInfo.setAsset(asset.orElse(null));

		Map<String,Object> metadata = new HashMap<>();

		if(withMetaData.isPresent()){

			metadata.put("connectionsCount",setUserConnections(user));
			metadata.put("followersCount",setUserFollowers(user));

			if(!referencedUserEmail.equals(presentUser.getEmail())){
				metadata.put("isFollowing",getIsFollowing(user, referencedUserEmail));
				metadata.put("isConnected",getConnection(user, referencedUserEmail));
			}


		}else{
			metadata.put("message","Pass withMetaData as true to view Metadata.");
		}

		userInfo.setMetadata(metadata);

		if (user.getProfileType() == null){

			userInfo.setProfile(null);

		}else {
			switch (user.getProfileType()) {
				case ATHLETES:
					userInfo.setProfile(athleteRepository.findByEmail(referencedUserEmail));
					break;
				case SCOUT:
					userInfo.setProfile(scoutRepository.findByEmail(referencedUserEmail));
					break;
				case TEAM:
					userInfo.setProfile(teamRepository.findByContactEmail(referencedUserEmail));
					break;
				case FANBASE:
					userInfo.setProfile(fanRepository.findByEmail(referencedUserEmail));
					break;
				default:
					userInfo.setProfile(null);
					break;
			}
		}

		if(!presentUser.getEmail().equals(referencedUserEmail)) {
			activityStatsService.registerProfileViews(userInfo.getUser().getEmail());
		}

		return userInfo;
	}

	private int setUserFollowers(User user) {
        return followerRepository.findByToEmail(user.getEmail()).size();
	}

	private int setUserConnections(User user) {
		int count = 0;
		count += connectionRepository.findByFromEmailAndStatus(user.getEmail(), InteractionStatus.ACCEPTED.toString()).size();
		count += connectionRepository.findByToEmailAndStatus(user.getEmail(), InteractionStatus.ACCEPTED.toString()).size();

		return count;
	}

	private boolean getIsFollowing(User presentUser, String referenceEmail) {
		return followerRepository.findByFromEmailAndToEmailOrToEmailAndFromEmail(presentUser.getEmail(), referenceEmail,presentUser.getEmail(), referenceEmail) != null;
	}

	private boolean getConnection(User presentUser, String referenceEmail) {
		return connectionRepository.findByFromEmailAndToEmailOrToEmailAndFromEmail(
				presentUser.getEmail(),
				referenceEmail,
				presentUser.getEmail(),
				referenceEmail
		).isPresent();
	}



	public AuthResponse<UserInfo> createUser(SignUpRequest signUpRequest) throws Exception {

		validateRequest(signUpRequest);
		User savedUser = null;

		String encodedPassword = PasswordUtil.encodePassword(signUpRequest.getPassword());

		User newUser = new User();
		newUser.setEmail(signUpRequest.getEmail());
		newUser.setFirstName(signUpRequest.getFirstName());
		newUser.setLastName(signUpRequest.getLastName());
		newUser.setCountry(signUpRequest.getCountry());
		newUser.setRegistationType(RegistrationType.EMAIL);
		newUser.setProfileType(signUpRequest.getProfileType());
		newUser.setPassword(encodedPassword);

		LocalDateTime now = LocalDateTime.now();
		newUser.setDateCreated(now);
		newUser.setDateUpdated(now);

		savedUser = userRepository.save(newUser);
		settingsService.initUserSetting(savedUser);
		otpService.generateAndSendOtp(savedUser.getEmail());

		AuthResponse<UserInfo> authResponse = new AuthResponse<UserInfo>(
				200,
				jwtUtil.generateToken(savedUser.getEmail()),
				"Successfully logged in",
				getUserByEmail(newUser, savedUser.getEmail(), Optional.of(false))
		);
		return authResponse;
	}

	public void validateRequest(SignUpRequest user){
		User db_user = userRepository.findByEmail(user.getEmail());
		System.out.println("================ printing the user ==========");
		System.out.println(user);
		if(db_user!= null ) {
			throw new CustomException("User already exists", new Throwable());
		}
	}

	public String authenticate(User user) {
		String email = user.getEmail();
		System.out.println("Authenticating user with email: " + email);

		String userStatus = checkUser(user);

		if ("Authenticated".equals(userStatus)) {
			return jwtUtil.generateToken(email);
		}
		return userStatus;
	}

	public String verifyCurrentPasswordAndSendOtp(String token, String currentPassword) {
		String email = jwtUtil.extractUsername(token);
		User user = Optional.ofNullable(userRepository.findByEmail(email))
				.orElseThrow(() -> new IllegalArgumentException("User not found."));

		if (!PasswordUtil.checkPassword(currentPassword, user.getPassword())) {
			throw new IllegalArgumentException("Current password is incorrect.");
		}

		try {
			otpService.generateAndSendOtp(email);
			System.out.println("Sending OTP to email: " + email);
			return "OTP sent to your email.";
		} catch (Exception e) {
			System.err.println("Failed to send OTP to email: " + email + ". Error: " + e.getMessage());
			return "There was an issue sending the OTP. Please try again later.";
		}
	}

	public String updatePassword(String token, PasswordUpdateRequest passwordUpdateRequest) {
		if (token == null || token.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization token is required.");
		}

		if (!passwordUpdateRequest.getNewPassword().equals(passwordUpdateRequest.getConfirmPassword())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match.");
		}

		if (!jwtUtil.isTokenValid(token)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token is invalid or expired.");
		}

		String email = jwtUtil.extractUsername(token);
		User user = Optional.ofNullable(userRepository.findByEmail(email))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

		if (!otpService.validateOtp(email, passwordUpdateRequest.getOtp())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired OTP.");
		}

		if (PasswordUtil.checkPassword(passwordUpdateRequest.getNewPassword(), user.getPassword())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must be different from the old password.");
		}

		user.setPassword(PasswordUtil.encodePassword(passwordUpdateRequest.getNewPassword()));
		userRepository.save(user);

		return "Password updated successfully.";
	}

	public String forgotPasswordRequest(ForgotPasswordRequest forgotPasswordRequest) {
		Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(forgotPasswordRequest.getEmail()));

		if (userOptional.isPresent()) {
			User user = userOptional.get();
			String token = jwtUtil.generateToken(String.valueOf(user.getId()));

			ConfirmationTokenModel confirmationToken = new ConfirmationTokenModel();
			confirmationToken.setToken(token);
			confirmationToken.setUser(user);
			confirmationToken.setCreatedAt(LocalDateTime.now());
			confirmationToken.setExpiresAt(LocalDateTime.now().plusHours(1));

			confirmationTokenRepository.save(confirmationToken);

			String resetUrl = emailUtil.getResetPasswordUrl(token);

			EmailDetails emailDetails = EmailDetails.builder()
					.fullName(user.getFirstName() + " " + user.getLastName())
					.recipient(user.getEmail())
					.link(resetUrl)
					.subject("GOLVIA PASSWORD RESET")
					.build();

			try {
				emailService.sendEmailAlerts(emailDetails, "forgot-password");
				return "Password reset email sent.";
			} catch (Exception e) {
				System.err.println("Error sending password reset email: " + e.getMessage());
				return "There was an issue sending the password reset email. Please try again later.";
			}
		}

		return "If the email exists in our system, you will receive a password reset email shortly.";
	}

	public String confirmResetPassword(String token, ConfirmForgotPassword confirmForgotPassword) {
		ConfirmationTokenModel confirmationToken = confirmationTokenRepository.findByToken(token)
				.orElseThrow(() -> new TokenExpiredException("Token not found or has expired."));

		if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new TokenExpiredException("Token has expired.");
		}

		if (!confirmForgotPassword.getNewPassword().equals(confirmForgotPassword.getConfirmPassword())) {
			throw new PasswordMismatchException("New passwords do not match.");
		}

		User user = confirmationToken.getUser();
		user.setPassword(PasswordUtil.encodePassword(confirmForgotPassword.getNewPassword()));
		userRepository.save(user);

		return "Password reset successfully.";
	}

	public Athlete upgradeProfile(Athlete athlete) {
		System.out.println("====================================");
		System.out.println(athlete);
		return athleteRepository.save(athlete);
	}

	public boolean updateProfileType(UpdateProfileTypeRequest req) {
		User user = userRepository.findByEmail(req.getEmail());

		if (user == null) {
			return false;
		}

		user.setProfileType(req.getProfileType());
		user.setSportType(req.getSportType());
		user.setTeamName(req.getTeamName());
		userRepository.save(user);

		return true;
	}

	public String confirmOTP(String email, String otp) {
		if (otpService.validateOtp(email, otp)) {
			User user = userRepository.findByEmail(email);
			user.setActive(true);
			userRepository.save(user);
			return jwtUtil.generateToken(email);
		}
		return "wrong otp";
	}

	public String checkUser(User user) {
		User dbUser = userRepository.findByEmail(user.getEmail());

		if (dbUser == null) {
			return "Denied";
		}

		if (!dbUser.isActive()) {
			return "InActive";
		}

		if(dbUser.getPassword() == null){
			return "Google-Account";
		}

		boolean isPasswordValid = PasswordUtil.checkPassword(user.getPassword(), dbUser.getPassword());

		System.out.println("isPasswordValid:" + isPasswordValid);

		if (isPasswordValid && dbUser.isActive()) {
			return "Authenticated";
		}

		if (!isPasswordValid) {
			return "Invalid";
		}

		return "Denied";
	}

	public String handle2FALogin(User user) throws MessagingException {
		String authenticationStatus = checkUser(user);

		User nUser;

		User dbUser = userRepository.findByEmail(user.getEmail());

		if(dbUser == null){
			nUser = user;
		}else{
			nUser = dbUser;
		}

		return switch (authenticationStatus) {
			case "Google-Account" -> "Google-Account";
			case "Denied" -> "Denied";
			case "Invalid" -> "Invalid";
			case "Authenticated" -> {
				otpService.generateAndSendCustomOtp(nUser);
				yield "2FA";
			}
			default -> "Unknown";
		};
	}


	@Transactional
	public String deleteUserByEmail(String email) {
		try {
			// Step 1: Find the user by email
			User user = userRepository.findByEmail(email);

			// Check if user is null
			if (user == null) {
				throw new UserNotFoundException("User not found");
			}
			// Step 2: Delete associated media files and posts
			deleteMediaAndPosts(user);

			// Step 3: Delete associated comments, likes, and the user
			deleteUserAssociations(user);

			// Step 5: Delete associated profile assets
			assetsRepository.deleteByUserId(user.getId());

			confirmationTokenRepository.deleteByUserId(user.getId());

			settingsRepository.deleteByEmail(user.getEmail());

			// Step 6: Return success message
			return "User and associated data deleted successfully";

		} catch (UserNotFoundException e) {
			logError("User not found: " + email, e);
			return e.getMessage();
		} catch (Exception e) {
			logError("Unexpected error occurred while deleting user: " + email, e);
			return "An unexpected error occurred: " + e.getMessage();
		}
	}

		private void deleteMediaAndPosts(User user) {
			List<Post> posts = postRepository.findByUser(user);
			for (Post post : posts) {
				// Delete media files associated with the post (from Cloudinary)
				post.getMediaUrls().forEach(media -> {
					try {
						postService.deleteMediaFromCloudinary(media);  // Reusing the deleteMediaFromCloudinary method
					} catch (Exception e) {
						logError("General error deleting media with publicId: " + media.getPublicId(), e);
					}
				});
			}
			// Step 3: Delete posts associated with the user
			postRepository.deleteAllByUser(user);
		}

		private void deleteUserAssociations(User user) {
			// Step 4: Delete comments associated with the user
			commentRepository.deleteAllByUser(user);

			// Step 5: Delete likes associated with the user
			likeRepository.deleteAllByUser(user);

			// Step 6: Delete the user
			userRepository.delete(user);
		}

		private void logError(String message, Exception e) {
			// Use a logging framework for better logging control (SLF4J, Log4j, etc.)
			LoggerFactory.getLogger(getClass()).error(message, e);
		}

		// Custom exception for user not found
		public static class UserNotFoundException extends RuntimeException {
			public UserNotFoundException(String message) {
				super(message);
			}
		}

	@Transactional
	public void cleanUpDuplicates() {
		cleanUpDuplicateUsers();
		cleanUpDuplicateAssets();
		cleanUpDuplicateProfiles();
		enforceUniqueConstraints();
	}


	// 1. Clean up duplicate users based on email
	private void cleanUpDuplicateUsers() {
		System.out.println("Cleaning up duplicate users...");
		userRepository.deleteDuplicateUsersByEmail();
		System.out.println("Duplicate users cleaned up successfully.");
		//
		userRepository.deleteDuplicateFanReferences();

		// Step 2: Delete duplicate entries from fans table
		userRepository.deleteDuplicateFans();
		// Step 1: Delete references in fan_favorite_athletes
		userRepository.deleteDuplicateFanFavoriteAthletes();

		// Step 2: Delete duplicate entries in fans table
		userRepository.deleteDuplicateFans();
	}

	// 2. Clean up duplicate assets based on user ID
	private void cleanUpDuplicateAssets() {
		System.out.println("Cleaning up duplicate assets...");
		assetRepository.deleteDuplicateAssetsByUserId();
		System.out.println("Duplicate assets cleaned up successfully.");
	}

	// 3. Clean up duplicate profiles in athletes, scouts, teams, and fans
	private void cleanUpDuplicateProfiles() {
		System.out.println("Cleaning up duplicate athlete profiles...");
		athleteRepository.deleteDuplicateAthletesByEmail();

		System.out.println("Cleaning up duplicate scout profiles...");
		scoutRepository.deleteDuplicateScoutsByEmail();

		System.out.println("Cleaning up duplicate team profiles...");
		teamRepository.deleteDuplicateTeamsByContactEmail();

		System.out.println("Cleaning up duplicate fanbase profiles...");
		fanRepository.deleteDuplicateFansByEmail();

		System.out.println("Duplicate profiles cleaned up successfully.");
	}


	// 4. Enforce unique constraints
	private void enforceUniqueConstraints() {
		System.out.println("Enforcing unique constraints...");

		// Enforce constraints programmatically
		userRepository.enforceUniqueConstraintOnEmail();
		assetRepository.enforceUniqueConstraintOnUserId();

		System.out.println("Unique constraints enforced successfully.");
	}
}

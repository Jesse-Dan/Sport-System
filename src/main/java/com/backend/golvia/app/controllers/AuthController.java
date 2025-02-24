package com.backend.golvia.app.controllers;

import com.backend.golvia.app.channels.LogChannel;
import com.backend.golvia.app.entities.Setting;
import com.backend.golvia.app.entities.UserActivity;
import com.backend.golvia.app.enums.RegistrationType;
import com.backend.golvia.app.models.response.AuthResponse;
import com.backend.golvia.app.repositories.activities.UserActivityRepository;
import com.backend.golvia.app.repositories.auth.UserRepository;
import com.backend.golvia.app.services.EmailService;
import com.backend.golvia.app.services.settings.SettingsService;
import com.backend.golvia.app.utilities.ResponseHelper;
import jakarta.mail.MessagingException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.golvia.app.entities.User;
import com.backend.golvia.app.models.UserInfo;
import com.backend.golvia.app.models.request.ConfirmForgotPassword;
import com.backend.golvia.app.models.request.ForgotPasswordRequest;
import com.backend.golvia.app.models.request.LoginRequest;
import com.backend.golvia.app.models.request.OtpRequest;
import com.backend.golvia.app.models.request.PasswordUpdateRequest;
import com.backend.golvia.app.models.request.PasswordVerificationRequest;
import com.backend.golvia.app.models.request.SignUpRequest;
import com.backend.golvia.app.models.request.UpdateProfileTypeRequest;
import com.backend.golvia.app.models.response.ApiResponse;
import com.backend.golvia.app.services.OtpService;
import com.backend.golvia.app.services.UserService;
import com.backend.golvia.app.utilities.JwtUtil;
import com.backend.golvia.app.utilities.ResponseUtil;

import jakarta.validation.Valid;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private UserService userService;

	@Autowired
	private OtpService otpService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SettingsService settingsService;

	@Autowired
	private EmailService emailService;

	private final UserActivityRepository userActivityRepository;

    public AuthController(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    @LogChannel
	@PostMapping("/send-mail")
	public ResponseEntity<?>  sendMail( @RequestBody EmailSenderDto emails){
		if (emails.getEmails().isEmpty()){
			return  ResponseHelper.internalServerError("Email Array must not be empty");
		}
			emails.getEmails().forEach(email->{
                try {
					emailService.sendMail(email,"Test","Testing with new email service",false);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            });

			return	ResponseHelper.created("email sent");

	}

	@Data
	static public class EmailSenderDto{
		private List<String> emails;
	}

	@LogChannel
	@PostMapping("/register")
	public ResponseEntity<Object> registerUser(@RequestBody SignUpRequest user) throws Exception {

		User db_user = userRepository.findByEmail(user.getEmail());
		System.out.println("================ printing the user ==========");
		
				System.out.println(user);
		if(db_user== null ) {
			
			AuthResponse<UserInfo> savedUser = userService.createUser(user);
			//Create UserActivity entry for the comment creation
			UserActivity userActivity = new UserActivity();
			userActivity.setUserIdentifier(user.getEmail()); // or use user.getId() if you want to store the user ID
			userActivity.setActivityType("User Created");
			userActivity.setTimestamp(LocalDateTime.now()); // Set current timestamp
			// Save the UserActivity in the user_activity table
			userActivityRepository.save(userActivity);
			return ResponseEntity.status(201).body(savedUser);
		
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ResponseUtil.bad_request("User already exists"));
		}
	}


	@LogChannel
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws Exception {
		System.out.println("Received email: " + loginRequest.getEmail());
		System.out.println("Received password: " + loginRequest.getPassword());

		// Check if email is null or empty
		if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ResponseUtil.auth_failure("Email cannot be null or empty"));
		}

		// Create user object
		User user = new User();
		user.setEmail(loginRequest.getEmail());
		user.setPassword(loginRequest.getPassword());

		if (user.getRegistationType() == RegistrationType.GOOGLE){
			return ResponseHelper.forbidden("Account was created with google.");
		}

		Setting userSettings =  settingsService.getUserSetting(user);
		String authenticationStatus = "Unknown";

		if (!userSettings.isHas2FA()) {
			authenticationStatus = userService.checkUser(user);
		} else {
			authenticationStatus = userService.handle2FALogin(user);
		}

		switch (authenticationStatus){
			case "Google-Account":
				return ResponseHelper.unprocessableEntity("Account was created with google-auth");
			case "Unknown":
				return ResponseHelper.unauthorized("Unknown Error Occurred");
			case "Denied":
				return ResponseHelper.forbidden("Invalid Credentials");
			case "Invalid":
				return ResponseHelper.forbidden("Invalid password");
			case "InActive":
				return ResponseHelper.forbidden("Email Not verified");
			case "2FA":
				return ResponseHelper.success(
						null,
						"2FA Otp Sent.",
						HttpStatus.ACCEPTED
				);
			case "Authenticated":

				String token = jwtUtil.generateToken(loginRequest.getEmail());
				UserInfo<?> userInfo = userService.getUserByEmail(user,loginRequest.getEmail(), Optional.of(false));

				AuthResponse<UserInfo> authResponse = new AuthResponse<UserInfo>(
						200,
						token ,
						"Successfully logged in",
						userInfo
				);

				return ResponseEntity.status(201).body(authResponse);

			default:
				return ResponseHelper.forbidden("Unknown Error Occurred");
		}
	}




	@LogChannel
	@PostMapping("/validateOTP")
	public ResponseEntity<?> validateOTP(@RequestBody OtpRequest details) {

		try{
			String token_response = userService.confirmOTP(details.getEmail(), details.getOtp());
			if (token_response == "wrong otp") {

				return ResponseEntity.status(401).body(ResponseUtil.auth_failure(token_response));

			}
			User user = userRepository.findByEmail(details.getEmail());
//
			UserInfo userInfo = userService.getUserByEmail(user,details.getEmail(), Optional.of(false));

			// Send the welcome email after OTP validation
			try {
				// Prepare the email context with the user information
				Context emailContext = emailService.prepareContext(userInfo);

				// Send the welcome email
				emailService.sendWelcomeEmail(user.getEmail(), "Welcome to Golvia!", emailContext);
			} catch (MessagingException e) {
				// Handle any email sending exceptions (Optional)
				e.printStackTrace();
			}

			return ResponseEntity.status(201).body(ResponseUtil.auth_success(token_response, userInfo));
		} catch (Exception e) {
			return ResponseHelper.unprocessableEntity(e.getMessage());
		}
	}


	@LogChannel
	@PostMapping("/updateProfileType")
	public ResponseEntity<Object> updatingProfileType(@RequestBody UpdateProfileTypeRequest req) {


		if (req == null) {

			return ResponseEntity.status(400).body(ResponseUtil.bad_request("bad request"));
		}

		boolean response = userService.updateProfileType(req);

		if (response) {

			return ResponseEntity.status(200).body(ResponseUtil.success(null));
			
		} else {

			return ResponseEntity.status(422).body(ResponseUtil.unprocessable_request("unprocessable request"));

		}
	}


	@LogChannel
	@PostMapping("/reset-password")
	public String verifyCurrentPasswordAndSendOtp(
			@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody PasswordVerificationRequest request) { // Use @RequestBody to accept the DTO
		// Use utility method to extract token
		String token = JwtUtil.extractTokenFromHeader(authorizationHeader);

		// Call the service to verify the token and extract the username
		return userService.verifyCurrentPasswordAndSendOtp(token, request.getCurrentPassword());
	}

	@LogChannel
	@PostMapping("/resend-otp")
	public ResponseEntity<ApiResponse<String>> resendOtp(@RequestHeader("Authorization") String authHeader) {
		try {
			// Step 1: Extract token from the Authorization header
			String token = jwtUtil.extractTokenFromHeader(authHeader); // Use the injected instance

			// Step 2: Extract email from the JWT token
			String email = jwtUtil.extractUsername(token); // Use the instance of JwtUtil

			// Step 3: Generate and send the OTP to the user's email
			otpService.generateAndSendOtp(email); // Calls the method to generate and send OTP

			// Step 4: Return a success response
			return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "OTP sent to your email.", null, null));

		} catch (Exception e) {
			// Handle errors (e.g., invalid token, email issues, etc.)
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error sending OTP. Please try again.", null, null));
		}
	}




	@PostMapping("/reset/update-password")
	public ResponseEntity<ApiResponse<String>> updatePassword(
			@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody PasswordUpdateRequest request) {
		// Extract token from header
		String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
		// Call the service to update the password, exact error messages will be passed
		String response = userService.updatePassword(token, request);
		return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), response, null, null));
	}


	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPasswordRequest(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
		String response = userService.forgotPasswordRequest(forgotPasswordRequest);
		return ResponseEntity.ok(response);
	}
	@PostMapping("/confirm-forgot-password")
	public ResponseEntity<?> confirmPasswordReset(@Valid @RequestBody ConfirmForgotPassword confirmForgotPassword) {
		// The token will now come from confirmPasswordRequest, not from the URL
		String response = userService.confirmResetPassword(confirmForgotPassword.getToken(), confirmForgotPassword);
		return ResponseEntity.ok(response);
	}



		@PostMapping("/cleanup-duplicates")
		public ResponseEntity<String> cleanUpDuplicates() {
			try {
				userService.cleanUpDuplicates();
				return ResponseEntity.ok("Duplicate records cleaned up successfully and unique constraints applied.");
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error during cleanup: " + e.getMessage());
			}
		}

}
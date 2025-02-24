package com.backend.golvia.app.services;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.backend.golvia.app.entities.User;
import com.backend.golvia.app.html.GolviaEmailBuilder;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.golvia.app.entities.OtpDetails;
import com.backend.golvia.app.repositories.auth.OtpRepository;

@Service
public class OtpService {

    private static final int EXPIRY_DURATION_MINUTES = 5;


    @Autowired
    OtpRepository otpRepository;


    @Autowired
    private EmailService emailService;


    public void generateAndSendOtp(String email) throws MessagingException {
        String otp = generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(EXPIRY_DURATION_MINUTES);

        // Save OTP details in the database
        OtpDetails otpDetails = new OtpDetails(otp, email, expiryTime);
        otpRepository.save(otpDetails);

        // Send email with OTP using EmailService
        String subject = "Golvia: Your OTP!";
        String body = "Your OTP is: " + otp + "\nPlease use this OTP within " + EXPIRY_DURATION_MINUTES + " minutes.";
        emailService.sendMail(email, subject, body, false); // Calls the EmailService method to send the email
    }

    private String generateOtp() {
        int otp = new SecureRandom().nextInt(900000) + 100000; // ensures 6 digits
        return String.valueOf(otp);
    }

    public boolean validateOtp(String email, String otp) {
        OtpDetails otpDetails = otpRepository.findByEmailAndOtp(email, otp);
        if (otpDetails == null || otpDetails.isExpired()) {
            return false; // Invalid or expired OTP
        }
        return true;
    }

    public void generateAndSendCustomOtp(User user) throws MessagingException {
        cleanupExpiredOtps();

        OtpDetails existingOtpDetails = otpRepository.findByEmail(user.getEmail());

        if (existingOtpDetails != null && !existingOtpDetails.isExpired()) {
            handleExistingOtp(user, existingOtpDetails);
        } else {
            generateAndSendNewOtp(user);
        }
    }

    private void cleanupExpiredOtps() {
        otpRepository.deleteAllExpired(LocalDateTime.now());
    }

    private void handleExistingOtp(User user, OtpDetails existingOtpDetails) throws MessagingException {
        String otp = existingOtpDetails.getOtp();
        LocalDateTime expiryTime = existingOtpDetails.getExpiryTime();

        String subject = "Golvia: Your Existing OTP!";
        String body = String.format(
                "Hello %s,%n\nYou already have a valid OTP: %s%n\n\nPlease use this OTP before %s.%n",
                user.getFirstName(), otp, expiryTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );

        GolviaEmailBuilder html = new GolviaEmailBuilder();
        html.setTitle(subject);
        html.setSubtitle(body);

        emailService.sendMail(user.getEmail(), subject, html.build(), true);
    }

    private void generateAndSendNewOtp(User user) throws MessagingException {
        String otp = generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(EXPIRY_DURATION_MINUTES);

        otpRepository.save(new OtpDetails(otp, user.getEmail(), expiryTime));

        String subject = "Golvia: Your OTP!";
        String body = String.format(
                "Hello %s,%n\nYour OTP is: %s%n\n\nPlease use this OTP within %d minutes.%n",
                user.getFirstName(), otp, EXPIRY_DURATION_MINUTES
        );

        GolviaEmailBuilder html = new GolviaEmailBuilder();
        html.setTitle(subject);
        html.setSubtitle(body);

        emailService.sendMail(user.getEmail(), subject, html.build(), true);
    }


}
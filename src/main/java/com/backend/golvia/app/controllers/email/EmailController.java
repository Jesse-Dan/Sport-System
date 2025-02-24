package com.backend.golvia.app.controllers.email;

import com.backend.golvia.app.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/send-welcome-to-all")
    public ResponseEntity<String> sendWelcomeEmailsToAll() {
        try {
            emailService.sendWelcomeEmailToAllUsers();
            return ResponseEntity.ok("Welcome emails have been sent successfully to all users!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send welcome emails: " + e.getMessage());
        }
    }
    @PostMapping("/send-welcome-to-specific")
    public ResponseEntity<String> sendWelcomeEmailsToSpecificUsers() {
        // List of emails to send to
        List<String> emails = Arrays.asList(
                "mowunmioke@gmail.com",
                "agathambrose@gmail.com",
                "nwankpa.Godson@yahoo.com",
                "eleven27creatives@gmail.com",
                "yemi@gol-via.com",
                "sylvelity009@yahoo.com",
                "agbe.terseer@gmail.com",
                "anyanwusylvy@outlook.com",
                "fredysallah@gmail.com",
                "nwankpagodson3@gmail.com",
                "gloriaprosper.og@gmail.com",
                "jesax013@gmail.com",
                "gloriaprosper.og@gmail.com",
                "ikisehtochukwu@gmail.com"
        );

        try {
            emailService.sendWelcomeEmailToSpecificUsers(emails);
            return ResponseEntity.ok("Welcome emails have been sent successfully to specified users!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send welcome emails: " + e.getMessage());
        }
    }

}

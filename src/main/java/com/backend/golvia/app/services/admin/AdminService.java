package com.backend.golvia.app.services.admin;

import com.backend.golvia.app.entities.User;
import com.backend.golvia.app.repositories.auth.UserRepository;
import com.backend.golvia.app.utilities.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    public void createAdminIfNotExist() {
        String defaultEmail = "admin@gol-via.com";
        String defaultPassword = "Golvia@1";
        String defaultFirstName = "Admin";  // Add first name
        String defaultLastName = "User";    // Add last name

        // Check if the admin already exists
        Optional<User> existingAdmin = Optional.ofNullable(userRepository.findByEmail(defaultEmail));

        if (existingAdmin.isPresent()) {
            System.out.println("Admin user already exists.");
            // If user exists, set 'active' to true (email verified)
            User admin = existingAdmin.get();
            admin.setActive(true);  // Set 'active' to true to simulate email verification
            userRepository.save(admin);    // Save the updated admin user
            System.out.println("Email verified automatically.");
        } else {
            // Admin does not exist, create a new one
            User admin = new User();
            admin.setEmail(defaultEmail);
            admin.setPassword(PasswordUtil.encodePassword(defaultPassword)); // Encrypt password
            admin.setFirstName(defaultFirstName);  // Set first name
            admin.setLastName(defaultLastName);    // Set last name
            admin.setActive(true);                 // Mark as active to simulate email verification

            userRepository.save(admin);
            System.out.println("Admin user created successfully with verified email.");
        }
    }
}

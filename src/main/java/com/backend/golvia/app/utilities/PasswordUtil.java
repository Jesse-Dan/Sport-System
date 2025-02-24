package com.backend.golvia.app.utilities;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Encode a raw password
    public static String encodePassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }
    // Verify if raw password matches encoded password
    public static boolean checkPassword(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }

    public static void main(String[] args) {
        // Example usage
        String rawPassword = "mysecretpassword";
        String hashedPassword = encodePassword(rawPassword);

        System.out.println("Raw Password: " + rawPassword);
        System.out.println("Hashed Password: " + hashedPassword);

        // Check if passwords match
        boolean isPasswordMatch = checkPassword(rawPassword, hashedPassword);
        System.out.println("Password match: " + isPasswordMatch);
    }
}


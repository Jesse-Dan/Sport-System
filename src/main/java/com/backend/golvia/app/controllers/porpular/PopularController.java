package com.backend.golvia.app.controllers.porpular;

import com.backend.golvia.app.models.response.ApiResponse;
import com.backend.golvia.app.services.porpular.PorpularService;
import com.backend.golvia.app.utilities.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clubs")
public class PopularController {

    private final PorpularService porpularService;
    private final JwtUtil jwtUtil;

    @GetMapping("/top/clubs")
    public ApiResponse<?> getTopClubs(HttpServletRequest request) throws Exception {
        // Extract email from the JWT token in the Authorization header
        String email = extractEmailFromToken(request);

        if (email == null) {
            // If no email is extracted, throw an exception
            throw new Exception("Authentication failed. Token is missing or invalid.");
        }

        // Call the service to get the top clubs and wrap it in try-catch block for exception propagation
        return porpularService.getTopClubs();
    }

    @GetMapping("/top/agents")
    public ApiResponse<?> getTopAgents(HttpServletRequest request) throws Exception {
        // Extract email from the JWT token in the Authorization header
        String email = extractEmailFromToken(request);

        if (email == null) {
            // If no email is extracted, throw an exception
            throw new Exception("Authentication failed. Token is missing or invalid.");
        }

        // Call the service to get the top agents and wrap it in try-catch block for exception propagation
        return porpularService.getTopAgents();
    }

    @GetMapping("/top-athletes")
    public ApiResponse<?> getTopAthletes(HttpServletRequest request) throws Exception {
        // Extract email from the JWT token in the Authorization header
        String email = extractEmailFromToken(request);

        if (email == null) {
            // If no email is extracted, throw an exception
            throw new Exception("Authentication failed. Token is missing or invalid.");
        }

        // Call the service to get the top athletes and wrap it in try-catch block for exception propagation
        return porpularService.getTopAthletes();
    }

    // Helper method to extract email from JWT token
    private String extractEmailFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractUsername(token); // Extracts email from the token
    }
}

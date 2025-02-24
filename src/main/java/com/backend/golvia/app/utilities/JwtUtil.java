package com.backend.golvia.app.utilities;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.backend.golvia.app.entities.User;
import com.backend.golvia.app.repositories.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.backend.golvia.app.exceptions.CustomException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;



@Component
public class JwtUtil {

    @Value("${security.jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    private final UserRepository userRepository;

    @Autowired
    public JwtUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String extractUsername(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new CustomException("JWT token is null or empty");
        }

        if (!isValidJwt(token)) {
            throw new CustomException("Invalid JWT token structure");
        }

        System.out.println("Extracting username from token: " + token);

        try {
            return extractAllClaims(token).getSubject();
        } catch (MalformedJwtException e) {
            throw new CustomException("Invalid JWT token: " + e.getMessage(), e);
        } catch (ExpiredJwtException e) {
            throw new CustomException("JWT token is expired: " + e.getMessage(), e);
        } catch (SignatureException e) {
            throw new CustomException("JWT signature does not match: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new CustomException("Could not extract username from token: " + e.getMessage(), e);
        }
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new CustomException("Failed to parse JWT: " + e.getMessage(), e);
        }
    }

    private boolean isTokenExpired(String token) {
        Date expirationDate = extractExpiration(token);
        return expirationDate.before(new Date());
    }

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            System.out.println("Token validation error: " + e.getMessage());
            return false;
        }
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    @SuppressWarnings("unused")
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours expiration
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public Boolean validateToken(String token) throws Exception {
        if (token == null || token.trim().isEmpty()) {
            throw new  Exception("JWT token is null or empty");
        }

        if (isTokenExpired(token)) {
            throw new Exception("JWT token is expired");
        }

        try {
            extractAllClaims(token);
            return true;
        } catch (SignatureException e) {
            throw new Exception("JWT signature does not match: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new Exception("Could not validate token: " + e.getMessage(), e);
        }
    }

    public static String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new CustomException("Authorization header is missing or invalid");
        }
        return authorizationHeader.substring(7);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            String token = getJwtFromRequest(request);
            if (token != null && !validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("JWT token is invalid");
                return;
            }
        } catch (ExpiredJwtException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT token has expired");
            return;
        } catch (CustomException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(ex.getMessage());
            return;
        } catch (Exception ex) {
            System.out.println("Error during JWT filter processing: " + ex.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Internal server error");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isValidJwt(String token) {
        String[] parts = token.split("\\.");
        return parts.length == 3;
    }

    // Get User entity from JWT token
    public User getUserFromToken(String authorizationHeader) throws Exception {
        System.out.println("Getting from header");

        String token = extractTokenFromHeader(authorizationHeader);

        System.out.println("Checking Expiration");

        if (isTokenExpired(token)) {
            throw new Exception("Token is expired");
        }

        System.out.println("Fetching Email");

        String email = extractUsername(token);

        System.out.println("User Email: " + email);

        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email));

        System.out.println("User Name: " + user.get().getFirstName());

        return user.orElseThrow(() -> new Exception("User not found"));
    }
}
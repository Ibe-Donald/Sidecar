package com.verveguard.sidecar.admin;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    // This secret key acts as the ink stamp. We will store it securely in the application.yml later.
    // It must be a long, random 256-bit string.
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    // The badge expires after 1 hour (3600000 milliseconds)
    private static final long EXPIRATION_TIME = 3600000;


     // Creates a new Digital ID Badge for the admin.

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }


     // Reads the name on the badge.

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }


     // Checks if the badge is real and hasn't expired.

    public boolean isTokenValid(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}

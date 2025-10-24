package com.blog.config;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
    private final String secretKey;
    private final long expTime;

    public JwtUtils(@Value("${security.jwt.secret-key}") String secretKey,
            @Value("${security.jwt.expiration-time}") long expTime) {
        this.secretKey = secretKey;
        this.expTime = expTime;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        return key;
    }

    public String generateToken(String userName, String role) {
        String token = Jwts.builder()
                .setSubject(userName)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
        return token;
    }

    public String extractUsername(String token) {
        try {
            String username = extractClaims(token).getSubject();
            return username;
        } catch (Exception e) {
            throw e;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractRole(String token) {
        try {
            String role = extractClaims(token).get("role", String.class);
            return role;
        } catch (Exception e) {
            System.err.println("Error extracting username: " + e.getMessage());
            throw e;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && isTokenExpired(token) == false;
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}

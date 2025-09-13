package com.blog.config;

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
        System.out.println("=== JWT DEBUG INFO ===");
        System.out.println("Secret Key: " + secretKey);
        System.out.println("Secret Key Length: " + secretKey.length());
        System.out.println("Expiration Time: " + expTime);
        System.out.println("=====================");
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        System.out.println("Generated signing key: " + key.getAlgorithm());
        return key;
    }

    public String generateToken(UserDetails userDetail) {
        String token = Jwts.builder()
                .setSubject(userDetail.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
        System.out.println("Generated token: " + token);
        return token;
    }

    public String extractUsername(String token) {
        System.out.println("Attempting to extract username from token: " + token.substring(0, 50) + "...");
        try {
            String username = extractClaims(token).getSubject();
            System.out.println("Successfully extracted username: " + username);
            return username;
        } catch (Exception e) {
            System.err.println("Error extracting username: " + e.getMessage());
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

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}

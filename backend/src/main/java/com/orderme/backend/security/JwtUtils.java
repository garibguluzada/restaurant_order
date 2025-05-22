package com.orderme.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5500")
@Component
public class JwtUtils {

    private final String jwtSecret = "supersecretkeysupersecretkeysupersecretkey"; // min 32 chars
    private final long jwtExpirationMs = 86400000; // 24 hours

    public String extractUsername(String token) {
        return getClaimsFromToken(token).getSubject();
    }
    
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token); // will throw if invalid
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}

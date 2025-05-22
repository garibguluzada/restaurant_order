package com.orderme.backend.controller;

import com.orderme.backend.security.JwtUtils;
import com.orderme.backend.security.TokenService;
import com.orderme.backend.table.RestaurantTable;
import com.orderme.backend.table.TableRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5500")
@RestController
@RequestMapping("/api/client")
public class ClientSessionController {

    private final JwtUtils jwtUtils;
    private final TokenService tokenService;
    private final TableRepository tableRepository;

    public ClientSessionController(JwtUtils jwtUtils, TokenService tokenService, TableRepository tableRepository) {
        this.jwtUtils = jwtUtils;
        this.tokenService = tokenService;
        this.tableRepository = tableRepository;
    }

    @GetMapping("/session")
    public ResponseEntity<?> getSession(@RequestParam String id, @CookieValue(value = "clientToken", required = false) String token, HttpServletResponse response) {
        String clientId = "client:" + id;
        
        // Check if token exists and is valid
        if (token != null && tokenService.validateToken(clientId, token)) {
            Claims claims = jwtUtils.getClaimsFromToken(token);
            return ResponseEntity.ok("Existing session for table id: " + claims.get("id"));
        }

        // Validate table ID against database
        boolean isValidTable = tableRepository.findById(id).isPresent();
        
        if (!isValidTable) {
            return ResponseEntity.status(401).body("Invalid table ID");
        }

        // Create JWT with id claim
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        String newToken = jwtUtils.generateToken(claims);

        // Store token in Redis
        tokenService.storeToken(clientId, newToken);

        ResponseCookie springCookie = ResponseCookie.from("clientToken", newToken)
            .httpOnly(true)
            .path("/")
            .sameSite("None")
            .secure(true)
            .build();
        response.addHeader("Set-Cookie", springCookie.toString());

        return ResponseEntity.ok("New session started for table id: " + id);
    }

    @PostMapping("/cashout")
    public ResponseEntity<?> endSession(
        @RequestHeader(value = "Authorization", required = false) String authHeader,
        @CookieValue(value = "clientToken", required = false) String tokenFromCookie,
        HttpServletResponse response) {
        
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else if (tokenFromCookie != null && !tokenFromCookie.isEmpty()) {
            token = tokenFromCookie;
        }

        if (token != null && !token.isEmpty()) {
            try {
                Claims claims = jwtUtils.getClaimsFromToken(token);
                String id = claims.get("id", String.class);
                if (id != null) {
                    tokenService.removeToken("client:" + id);
                    System.out.println("Removed token from Redis for client ID: " + id);
                } else {
                    System.out.println("ID not found in token");
                }
            } catch (Exception e) {
                System.err.println("Error parsing token: " + e.getMessage());
            }
        } else {
            System.out.println("No valid token found in request");
        }

        // Clear the HttpOnly cookie
        ResponseCookie deleteSpringCookie = ResponseCookie.from("clientToken", "")
            .httpOnly(true)
            .path("/")
            .maxAge(0)
            .sameSite("None")
            .secure(true)
            .build();
        response.addHeader("Set-Cookie", deleteSpringCookie.toString());

        return ResponseEntity.ok("Session ended");
    }
}

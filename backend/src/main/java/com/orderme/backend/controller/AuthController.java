package com.orderme.backend.controller;

import com.orderme.backend.staff.Staff;
import com.orderme.backend.security.JwtUtils;
import com.orderme.backend.security.TokenService;
import com.orderme.backend.staff.StaffService;
import com.orderme.backend.table.TableRepository;
import com.orderme.backend.table.RestaurantTable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;
import com.orderme.backend.dto.LoginRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5500", allowCredentials = "true", allowedHeaders = "*", methods = {
        RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS })
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtils jwtUtils;
    private final TokenService tokenService;
    private final StaffService staffService;
    private final TableRepository tableRepository;

    public AuthController(JwtUtils jwtUtils, TokenService tokenService, StaffService staffService, TableRepository tableRepository) {
        this.jwtUtils = jwtUtils;
        this.tokenService = tokenService;
        this.staffService = staffService;
        this.tableRepository = tableRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        // Authenticate the user
        Staff staff = staffService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

        // Check if the staff object is null (invalid credentials)
        if (staff == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        // JWT creation and cookie handling
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", staff.getUsername());
        claims.put("role", staff.getRole());
        String token = jwtUtils.generateToken(claims);

        // Store token in Redis
        tokenService.storeToken(staff.getUsername(), token);

        Cookie cookie = new Cookie("staffToken", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
        @RequestHeader(value = "Authorization", required = false) String authHeader,
        @CookieValue(name = "staffToken", required = false) String tokenFromCookie,
        HttpServletResponse response) {
        
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else if (tokenFromCookie != null && !tokenFromCookie.isEmpty()) {
            token = tokenFromCookie;
        }

        if (token != null && !token.isEmpty()) {
            try {
                var claims = jwtUtils.getClaimsFromToken(token);
                String username = (String) claims.get("username");
                if (username != null) {
                    tokenService.removeToken(username);
                    System.out.println("Removed token from Redis for staff username: " + username);
                } else {
                    System.out.println("Username not found in token");
                }
            } catch (Exception e) {
                System.err.println("Error parsing token: " + e.getMessage());
            }
        } else {
            System.out.println("No valid token found in request");
        }

        // Clear the HttpOnly cookie
        Cookie cookie = new Cookie("staffToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok("Logout successful");
    }

    @PostMapping("/id-login")
    public ResponseEntity<?> idLogin(@RequestBody Map<String, String> body, HttpServletResponse response) {
        String id = body.get("id");
        if (id == null) {
            return ResponseEntity.badRequest().body("Missing ID");
        }

        // Validate table ID against database
        boolean isValidTable = tableRepository.findById(id).isPresent();
        if (!isValidTable) {
            return ResponseEntity.status(401).body("Invalid table ID");
        }

        // Create JWT with id claim
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        String token = jwtUtils.generateToken(claims);

        // Store token in Redis with a special prefix for client tokens
        tokenService.storeToken("client:" + id, token);

        ResponseCookie springCookie = ResponseCookie.from("clientToken", token)
            .httpOnly(true)
            .path("/")
            .sameSite("None")
            .secure(true)
            .build();
        response.addHeader("Set-Cookie", springCookie.toString());

        return ResponseEntity.ok(Map.of("token", token));
    }
}

package com.grocerystore.controller;

import com.grocerystore.dto.auth.JwtResponse;
import com.grocerystore.dto.auth.LoginRequest;
import com.grocerystore.dto.auth.RegisterRequest;
import com.grocerystore.dto.common.ApiResponse;
import com.grocerystore.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    /**
     * Authenticate user and return JWT token.
     * 
     * @param loginRequest login credentials
     * @return JWT response with token and user details
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<ApiResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for email: {}", loginRequest.getEmail());
        
        try {
            JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
            logger.info("Login successful for email: {}", loginRequest.getEmail());
            
            return ResponseEntity.ok(ApiResponse.success("Login successful", jwtResponse));
        } catch (Exception e) {
            logger.error("Login failed for email: {}", loginRequest.getEmail(), e);
            throw e;
        }
    }

    /**
     * Register a new user.
     * 
     * @param registerRequest registration details
     * @return JWT response with token and user details
     */
    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user and return JWT token")
    public ResponseEntity<ApiResponse<JwtResponse>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.info("Registration attempt for email: {}", registerRequest.getEmail());
        
        try {
            JwtResponse jwtResponse = authService.registerUser(registerRequest);
            logger.info("Registration successful for email: {}", registerRequest.getEmail());
            
            return ResponseEntity.ok(ApiResponse.success("Registration successful", jwtResponse));
        } catch (Exception e) {
            logger.error("Registration failed for email: {}", registerRequest.getEmail(), e);
            throw e;
        }
    }

    /**
     * Refresh JWT token.
     * 
     * @param request HTTP request containing Authorization header
     * @return new JWT response
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Refresh JWT token")
    public ResponseEntity<ApiResponse<JwtResponse>> refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Invalid authorization header for token refresh");
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Invalid authorization header"));
        }
        
        String token = authHeader.substring(7);
        
        try {
            JwtResponse jwtResponse = authService.refreshToken(token);
            logger.info("Token refreshed successfully");
            
            return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", jwtResponse));
        } catch (Exception e) {
            logger.error("Token refresh failed", e);
            throw e;
        }
    }

    /**
     * Validate JWT token.
     * 
     * @param request HTTP request containing Authorization header
     * @return validation result
     */
    @PostMapping("/validate")
    @Operation(summary = "Validate token", description = "Validate JWT token")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(ApiResponse.success("Token validation result", false));
        }
        
        String token = authHeader.substring(7);
        boolean isValid = authService.validateToken(token);
        
        return ResponseEntity.ok(ApiResponse.success("Token validation result", isValid));
    }

    /**
     * Logout user (client-side token removal).
     * 
     * @return logout confirmation
     */
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user (client should remove token)")
    public ResponseEntity<ApiResponse<String>> logout() {
        logger.info("User logout requested");
        
        // Since JWT is stateless, logout is handled on client-side by removing the token
        // This endpoint is provided for consistency and potential future server-side logout logic
        
        return ResponseEntity.ok(ApiResponse.success("Logout successful. Please remove the token from client storage."));
    }
}

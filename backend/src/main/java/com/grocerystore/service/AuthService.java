package com.grocerystore.service;

import com.grocerystore.dto.auth.JwtResponse;
import com.grocerystore.dto.auth.LoginRequest;
import com.grocerystore.dto.auth.RegisterRequest;
import com.grocerystore.entity.User;
import com.grocerystore.entity.UserRole;
import com.grocerystore.exception.BadRequestException;
import com.grocerystore.exception.ResourceNotFoundException;
import com.grocerystore.repository.UserRepository;
import com.grocerystore.security.CustomUserDetailsService;
import com.grocerystore.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for authentication operations.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */
@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * Authenticate user and generate JWT token.
     * 
     * @param loginRequest login credentials
     * @return JWT response with token and user details
     */
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        logger.info("Authenticating user: {}", loginRequest.getEmail());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get user details
        CustomUserDetailsService.UserPrincipal userPrincipal = 
            (CustomUserDetailsService.UserPrincipal) authentication.getPrincipal();

        // Generate JWT token
        String jwt = jwtUtil.generateToken(userPrincipal);

        // Get user entity for additional details
        User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        logger.info("User authenticated successfully: {}", user.getEmail());

        return new JwtResponse(
            jwt,
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getRole(),
            jwtUtil.getJwtExpirationMs()
        );
    }

    /**
     * Register a new user.
     * 
     * @param registerRequest registration details
     * @return JWT response with token and user details
     */
    public JwtResponse registerUser(RegisterRequest registerRequest) {
        logger.info("Registering new user: {}", registerRequest.getEmail());

        // Validate request
        validateRegistrationRequest(registerRequest);

        // Check if user already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        // Create new user
        User user = new User();
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setAddress(registerRequest.getAddress());
        user.setContactNumber(registerRequest.getContactNumber());
        user.setRole(UserRole.CUSTOMER);

        // Save user
        user = userRepository.save(user);

        logger.info("User registered successfully: {}", user.getEmail());

        // Authenticate the newly registered user
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String jwt = jwtUtil.generateToken(userDetails);

        return new JwtResponse(
            jwt,
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getRole(),
            jwtUtil.getJwtExpirationMs()
        );
    }

    /**
     * Validate JWT token.
     * 
     * @param token JWT token
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    /**
     * Get user details from JWT token.
     * 
     * @param token JWT token
     * @return user details
     */
    public UserDetails getUserFromToken(String token) {
        String username = jwtUtil.extractUsername(token);
        return userDetailsService.loadUserByUsername(username);
    }

    /**
     * Refresh JWT token.
     * 
     * @param token current JWT token
     * @return new JWT response
     */
    public JwtResponse refreshToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new BadRequestException("Invalid token");
        }

        String username = jwtUtil.extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String newToken = jwtUtil.generateToken(userDetails);

        return new JwtResponse(
            newToken,
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getRole(),
            jwtUtil.getJwtExpirationMs()
        );
    }

    /**
     * Validate registration request.
     * 
     * @param request registration request
     */
    private void validateRegistrationRequest(RegisterRequest request) {
        if (!request.isPasswordMatching()) {
            throw new BadRequestException("Passwords do not match");
        }
    }
}

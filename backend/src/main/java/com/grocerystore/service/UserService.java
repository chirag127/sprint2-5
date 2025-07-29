package com.grocerystore.service;

import com.grocerystore.dto.user.ChangePasswordRequest;
import com.grocerystore.dto.user.UpdateProfileRequest;
import com.grocerystore.dto.user.UserResponse;
import com.grocerystore.entity.User;
import com.grocerystore.entity.UserRole;
import com.grocerystore.exception.BadRequestException;
import com.grocerystore.exception.ResourceNotFoundException;
import com.grocerystore.repository.UserRepository;
import com.grocerystore.security.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for user operations.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */
@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Get current authenticated user.
     * 
     * @return current user
     */
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("No authenticated user found");
        }

        CustomUserDetailsService.UserPrincipal userPrincipal = 
            (CustomUserDetailsService.UserPrincipal) authentication.getPrincipal();

        return userRepository.findById(userPrincipal.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Get current user profile.
     * 
     * @return user response DTO
     */
    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile() {
        User user = getCurrentUser();
        return UserResponse.fromUser(user);
    }

    /**
     * Get user by ID.
     * 
     * @param userId user ID
     * @return user response DTO
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return UserResponse.fromUser(user);
    }

    /**
     * Get user by email.
     * 
     * @param email user email
     * @return user response DTO
     */
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return UserResponse.fromUser(user);
    }

    /**
     * Get all users with pagination.
     * 
     * @param pageable pagination information
     * @return page of user response DTOs
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(UserResponse::fromUser);
    }

    /**
     * Get users by role.
     * 
     * @param role user role
     * @param pageable pagination information
     * @return page of user response DTOs
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsersByRole(UserRole role, Pageable pageable) {
        Page<User> users = userRepository.findByRole(role, pageable);
        return users.map(UserResponse::fromUser);
    }

    /**
     * Search users by name or email.
     * 
     * @param searchTerm search term
     * @param pageable pagination information
     * @return page of user response DTOs
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String searchTerm, Pageable pageable) {
        Page<User> users = userRepository.searchUsers(searchTerm, pageable);
        return users.map(UserResponse::fromUser);
    }

    /**
     * Update current user profile.
     * 
     * @param request update profile request
     * @return updated user response DTO
     */
    public UserResponse updateProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();
        
        logger.info("Updating profile for user: {}", user.getEmail());

        user.setFullName(request.getFullName());
        user.setAddress(request.getAddress());
        user.setContactNumber(request.getContactNumber());

        user = userRepository.save(user);
        
        logger.info("Profile updated successfully for user: {}", user.getEmail());
        
        return UserResponse.fromUser(user);
    }

    /**
     * Change current user password.
     * 
     * @param request change password request
     */
    public void changePassword(ChangePasswordRequest request) {
        User user = getCurrentUser();
        
        logger.info("Changing password for user: {}", user.getEmail());

        // Validate request
        if (!request.isNewPasswordMatching()) {
            throw new BadRequestException("New passwords do not match");
        }

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        logger.info("Password changed successfully for user: {}", user.getEmail());
    }

    /**
     * Delete user by ID (admin only).
     * 
     * @param userId user ID
     */
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        logger.info("Deleting user: {}", user.getEmail());
        
        userRepository.delete(user);
        
        logger.info("User deleted successfully: {}", user.getEmail());
    }

    /**
     * Update user role (admin only).
     * 
     * @param userId user ID
     * @param role new role
     * @return updated user response DTO
     */
    public UserResponse updateUserRole(UUID userId, UserRole role) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        logger.info("Updating role for user: {} from {} to {}", user.getEmail(), user.getRole(), role);
        
        user.setRole(role);
        user = userRepository.save(user);
        
        logger.info("Role updated successfully for user: {}", user.getEmail());
        
        return UserResponse.fromUser(user);
    }

    /**
     * Get user statistics.
     * 
     * @return user statistics
     */
    @Transactional(readOnly = true)
    public UserStatistics getUserStatistics() {
        long totalUsers = userRepository.count();
        long totalCustomers = userRepository.countCustomers();
        long totalAdmins = userRepository.countAdmins();
        
        return new UserStatistics(totalUsers, totalCustomers, totalAdmins);
    }

    /**
     * Inner class for user statistics.
     */
    public static class UserStatistics {
        private final long totalUsers;
        private final long totalCustomers;
        private final long totalAdmins;

        public UserStatistics(long totalUsers, long totalCustomers, long totalAdmins) {
            this.totalUsers = totalUsers;
            this.totalCustomers = totalCustomers;
            this.totalAdmins = totalAdmins;
        }

        public long getTotalUsers() { return totalUsers; }
        public long getTotalCustomers() { return totalCustomers; }
        public long getTotalAdmins() { return totalAdmins; }
    }
}

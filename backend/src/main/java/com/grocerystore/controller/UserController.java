package com.grocerystore.controller;

import com.grocerystore.dto.common.ApiResponse;
import com.grocerystore.dto.common.PageResponse;
import com.grocerystore.dto.user.ChangePasswordRequest;
import com.grocerystore.dto.user.UpdateProfileRequest;
import com.grocerystore.dto.user.UserResponse;
import com.grocerystore.entity.UserRole;
import com.grocerystore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for user operations.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "User management APIs")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * Get current user profile.
     * 
     * @return current user profile
     */
    @GetMapping("/profile")
    @Operation(summary = "Get current user profile", description = "Get the profile of the currently authenticated user")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUserProfile() {
        logger.info("Getting current user profile");
        
        UserResponse userResponse = userService.getCurrentUserProfile();
        
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved successfully", userResponse));
    }

    /**
     * Update current user profile.
     * 
     * @param request update profile request
     * @return updated user profile
     */
    @PutMapping("/profile")
    @Operation(summary = "Update current user profile", description = "Update the profile of the currently authenticated user")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        logger.info("Updating current user profile");
        
        UserResponse userResponse = userService.updateProfile(request);
        
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", userResponse));
    }

    /**
     * Change current user password.
     * 
     * @param request change password request
     * @return success message
     */
    @PutMapping("/change-password")
    @Operation(summary = "Change password", description = "Change the password of the currently authenticated user")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        logger.info("Changing password for current user");
        
        userService.changePassword(request);
        
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

    /**
     * Get user by ID (admin only).
     * 
     * @param userId user ID
     * @return user details
     */
    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Get user details by ID (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID userId) {
        logger.info("Getting user by ID: {}", userId);
        
        UserResponse userResponse = userService.getUserById(userId);
        
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", userResponse));
    }

    /**
     * Get all users with pagination (admin only).
     * 
     * @param page page number (0-based)
     * @param size page size
     * @param sortBy sort field
     * @param sortDir sort direction
     * @return paginated list of users
     */
    @GetMapping
    @Operation(summary = "Get all users", description = "Get all users with pagination (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        logger.info("Getting all users - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<UserResponse> users = userService.getAllUsers(pageable);
        PageResponse<UserResponse> pageResponse = PageResponse.fromPage(users);
        
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", pageResponse));
    }

    /**
     * Search users by name or email (admin only).
     * 
     * @param searchTerm search term
     * @param page page number (0-based)
     * @param size page size
     * @return paginated list of users
     */
    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users by name or email (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> searchUsers(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.info("Searching users with term: {}", searchTerm);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<UserResponse> users = userService.searchUsers(searchTerm, pageable);
        PageResponse<UserResponse> pageResponse = PageResponse.fromPage(users);
        
        return ResponseEntity.ok(ApiResponse.success("Users search completed", pageResponse));
    }

    /**
     * Get users by role (admin only).
     * 
     * @param role user role
     * @param page page number (0-based)
     * @param size page size
     * @return paginated list of users
     */
    @GetMapping("/role/{role}")
    @Operation(summary = "Get users by role", description = "Get users by role (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUsersByRole(
            @PathVariable UserRole role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.info("Getting users by role: {}", role);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<UserResponse> users = userService.getUsersByRole(role, pageable);
        PageResponse<UserResponse> pageResponse = PageResponse.fromPage(users);
        
        return ResponseEntity.ok(ApiResponse.success("Users retrieved by role successfully", pageResponse));
    }

    /**
     * Update user role (admin only).
     * 
     * @param userId user ID
     * @param role new role
     * @return updated user details
     */
    @PutMapping("/{userId}/role")
    @Operation(summary = "Update user role", description = "Update user role (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
            @PathVariable UUID userId,
            @RequestParam UserRole role) {
        
        logger.info("Updating role for user: {} to: {}", userId, role);
        
        UserResponse userResponse = userService.updateUserRole(userId, role);
        
        return ResponseEntity.ok(ApiResponse.success("User role updated successfully", userResponse));
    }

    /**
     * Delete user (admin only).
     * 
     * @param userId user ID
     * @return success message
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user", description = "Delete user (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable UUID userId) {
        logger.info("Deleting user: {}", userId);
        
        userService.deleteUser(userId);
        
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }

    /**
     * Get user statistics (admin only).
     * 
     * @return user statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get user statistics", description = "Get user statistics (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserService.UserStatistics>> getUserStatistics() {
        logger.info("Getting user statistics");
        
        UserService.UserStatistics statistics = userService.getUserStatistics();
        
        return ResponseEntity.ok(ApiResponse.success("User statistics retrieved successfully", statistics));
    }
}

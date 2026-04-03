package org.example.finaccesscore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finaccesscore.dto.CreateUserRequest;
import org.example.finaccesscore.dto.RegisterUserRequest;
import org.example.finaccesscore.dto.UpdateUserRequest;
import org.example.finaccesscore.dto.UserResponse;
import org.example.finaccesscore.exception.DuplicateResourceException;
import org.example.finaccesscore.exception.ResourceNotFoundException;
import org.example.finaccesscore.mapper.UserMapper;
import org.example.finaccesscore.model.Role;
import org.example.finaccesscore.model.User;
import org.example.finaccesscore.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for user management operations.
 * Handles user registration, creation, updates, and deactivation with proper validation and security.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user with default VIEWER role (public self-registration).
     * 
     * @param request the registration request containing username, email, and password
     * @return UserResponse containing the created user details
     * @throws DuplicateResourceException if username or email already exists
     */
    @Transactional
    public UserResponse registerUser(RegisterUserRequest request) {
        log.info("Registering new user: username={}, email={}", request.getUsername(), request.getEmail());
        
        validateUniqueUser(request.getUsername(), request.getEmail(), "Registration");
        
        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        
        UserResponse response = userMapper.toResponse(userRepository.save(user));
        log.info("User registered successfully: id={}, username={}", response.getId(), response.getUsername());
        return response;
    }

    /**
     * Create a new user with specific roles (Admin only).
     * 
     * @param request the creation request containing username, email, password, and roles
     * @return UserResponse containing the created user details
     * @throws DuplicateResourceException if username or email already exists
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Admin creating user: username={}, email={}, roles={}", 
            request.getUsername(), request.getEmail(), request.getRoles());
        
        validateUniqueUser(request.getUsername(), request.getEmail(), "User creation");
        
        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        
        UserResponse response = userMapper.toResponse(userRepository.save(user));
        log.info("User created successfully by admin: id={}, username={}", response.getId(), response.getUsername());
        return response;
    }

    /**
     * Get all users without pagination.
     * Note: For large datasets, consider using getAllUsersPaginated() instead.
     * 
     * @return List of all users
     */
    public List<UserResponse> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all users with pagination support.
     * 
     * @param pageable pagination parameters (page, size, sort)
     * @return Page of users
     */
    public Page<UserResponse> getAllUsersPaginated(Pageable pageable) {
        log.debug("Fetching users: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    /**
     * Get a user by ID.
     * 
     * @param id the user ID
     * @return UserResponse containing the user details
     * @throws ResourceNotFoundException if user not found
     */
    public UserResponse getUserById(Long id) {
        log.debug("Fetching user with id={}", id);
        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("User not found with id={}", id);
                    return new ResourceNotFoundException("User", "id", id);
                });
    }

    /**
     * Update an existing user.
     * 
     * @param id the user ID to update
     * @param request the update request containing fields to update
     * @return UserResponse containing the updated user details
     * @throws ResourceNotFoundException if user not found
     * @throws DuplicateResourceException if new username or email already exists
     */
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        log.info("Updating user with id={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found for update with id={}", id);
                    return new ResourceNotFoundException("User", "id", id);
                });
        
        validateUniqueUserForUpdate(user, request);
        
        userMapper.applyUpdate(request, user);
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            log.debug("Password updated for user id={}", id);
        }
        
        UserResponse response = userMapper.toResponse(userRepository.save(user));
        log.info("User updated successfully: id={}, username={}", id, response.getUsername());
        return response;
    }

    /**
     * Deactivate a user (soft delete).
     * Validates that the last admin user cannot be deactivated.
     * 
     * @param id the user ID to deactivate
     * @throws ResourceNotFoundException if user not found
     * @throws IllegalStateException if attempting to deactivate the last admin
     */
    @Transactional
    public void deactivateUser(Long id) {
        log.info("Deactivating user with id={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found for deactivation with id={}", id);
                    return new ResourceNotFoundException("User", "id", id);
                });
        
        validateCanDeactivate(user);
        
        user.setActive(false);
        userRepository.save(user);
        log.info("User deactivated successfully: id={}, username={}", id, user.getUsername());
    }

    // ==================== Private Helper Methods ====================

    /**
     * Validate that username and email are unique.
     * 
     * @param username the username to validate
     * @param email the email to validate
     * @param operation the operation name for logging
     * @throws DuplicateResourceException if username or email already exists
     */
    private void validateUniqueUser(String username, String email, String operation) {
        if (userRepository.findByUsername(username).isPresent()) {
            log.warn("{} failed: username already exists: {}", operation, username);
            throw new DuplicateResourceException("User", "username", username);
        }
        if (userRepository.findByEmail(email).isPresent()) {
            log.warn("{} failed: email already exists: {}", operation, email);
            throw new DuplicateResourceException("User", "email", email);
        }
    }

    /**
     * Validate uniqueness for user update operation.
     * Only checks if the field is being changed.
     * 
     * @param existingUser the existing user entity
     * @param request the update request
     * @throws DuplicateResourceException if new username or email already exists
     */
    private void validateUniqueUserForUpdate(User existingUser, UpdateUserRequest request) {
        if (request.getUsername() != null && !request.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                log.warn("Update failed: username already exists: {}", request.getUsername());
                throw new DuplicateResourceException("User", "username", request.getUsername());
            }
        }
        
        if (request.getEmail() != null && !request.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                log.warn("Update failed: email already exists: {}", request.getEmail());
                throw new DuplicateResourceException("User", "email", request.getEmail());
            }
        }
    }

    /**
     * Validate that a user can be deactivated.
     * Business rule: Cannot deactivate the last active admin user.
     * 
     * @param user the user to validate
     * @throws IllegalStateException if attempting to deactivate the last admin
     */
    private void validateCanDeactivate(User user) {
        if (user.getRoles().contains(Role.ROLE_ADMIN)) {
            long activeAdminCount = userRepository.countByRolesContainingAndActiveTrue(Role.ROLE_ADMIN);
            if (activeAdminCount <= 1) {
                log.warn("Cannot deactivate last admin user: id={}, username={}", user.getId(), user.getUsername());
                throw new IllegalStateException("Cannot deactivate the last admin user. At least one admin must remain active.");
            }
        }
    }
}

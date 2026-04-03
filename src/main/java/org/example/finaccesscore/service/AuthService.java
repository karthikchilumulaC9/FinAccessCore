package org.example.finaccesscore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finaccesscore.dto.LoginRequest;
import org.example.finaccesscore.dto.LoginResponse;
import org.example.finaccesscore.exception.ResourceNotFoundException;
import org.example.finaccesscore.model.User;
import org.example.finaccesscore.repository.UserRepository;
import org.example.finaccesscore.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for authentication operations including login and token generation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * Authenticate user and generate JWT token.
     * 
     * @param request login credentials (username and password)
     * @return LoginResponse containing JWT token and user details
     * @throws BadCredentialsException if credentials are invalid
     * @throws ResourceNotFoundException if user not found
     */
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String token = jwtUtil.generateToken(userDetails);
            
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "username", request.getUsername()));
            
            if (!user.getActive()) {
                log.warn("Login failed: user account is deactivated: {}", request.getUsername());
                throw new BadCredentialsException("User account is deactivated");
            }
            
            log.info("Login successful for user: {}", request.getUsername());
            return new LoginResponse(token, user.getId(), user.getUsername(), user.getEmail(), user.getRoles());
            
        } catch (BadCredentialsException e) {
            log.warn("Login failed: invalid credentials for username: {}", request.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}

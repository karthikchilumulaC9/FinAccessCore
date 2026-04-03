package org.example.finaccesscore.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.finaccesscore.dto.LoginRequest;
import org.example.finaccesscore.dto.LoginResponse;
import org.example.finaccesscore.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for authentication endpoints (login).
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    /**
     * Login endpoint - returns JWT token for authenticated users.
     * 
     * @param request login credentials
     * @return LoginResponse with JWT token and user details
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}

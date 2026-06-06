package com.hms.auth_service.controller;

import com.hms.auth_service.dto.request.LoginRequest;
import com.hms.auth_service.dto.request.RegisterRequest;
import com.hms.auth_service.dto.response.AuthResponse;
import com.hms.auth_service.response.ApiResponse;
import com.hms.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Login successful", response));
    }

    @PutMapping("/update-status/{authId}")
    public ResponseEntity<ApiResponse<AuthResponse>> updateStatus(
            @PathVariable("authId") Long authId,
            @RequestBody Map<String, String> request) {
        AuthResponse response = authService.updateStatus(authId, request.get("status"));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Status updated successfully", response));
    }
}

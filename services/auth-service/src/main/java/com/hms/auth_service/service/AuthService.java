package com.hms.auth_service.service;

import com.hms.auth_service.dto.request.LoginRequest;
import com.hms.auth_service.dto.request.RegisterRequest;
import com.hms.auth_service.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse updateStatus(Long authId, String status);
}

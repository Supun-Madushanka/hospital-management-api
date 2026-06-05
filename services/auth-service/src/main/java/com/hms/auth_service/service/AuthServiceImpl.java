package com.hms.auth_service.service;

import com.hms.auth_service.dto.request.LoginRequest;
import com.hms.auth_service.dto.request.RegisterRequest;
import com.hms.auth_service.dto.response.AuthResponse;
import com.hms.auth_service.entity.Credential;
import com.hms.auth_service.enums.UserStatus;
import com.hms.auth_service.exception.ResourceNotFoundException;
import com.hms.auth_service.exception.UnauthorizedException;
import com.hms.auth_service.repository.CredentialRepository;
import com.hms.auth_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final CredentialRepository credentialRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (credentialRepository.existsByEmail(request.getEmail())) {
            throw new UnauthorizedException("Email already exists");
        }

        UserStatus status = switch (request.getRole()) {
            case DOCTOR -> UserStatus.PENDING;
            case PATIENT, ADMIN -> UserStatus.ACTIVE;
        };

        Credential credential = Credential.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .status(status)
                .build();

        Credential saved = credentialRepository.save(credential);

        String token = null;
        if (status == UserStatus.ACTIVE) {
            token = jwtService.generateToken(saved);
        }

        return AuthResponse.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .role(saved.getRole())
                .status(saved.getStatus())
                .token(token)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        Credential credential = credentialRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), credential.getPassword())) {
            throw new UnauthorizedException("Invalid password");
        }

        if (credential.getStatus() == UserStatus.PENDING) {
            throw new UnauthorizedException("Your account is pending approval");
        }

        if (credential.getStatus() == UserStatus.REJECTED) {
            throw new UnauthorizedException("Your account has been rejected");
        }

        String token = jwtService.generateToken(credential);

        return AuthResponse.builder()
                .id(credential.getId())
                .email(credential.getEmail())
                .role(credential.getRole())
                .status(credential.getStatus())
                .token(token)
                .build();
    }
}

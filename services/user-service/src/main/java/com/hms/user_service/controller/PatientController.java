package com.hms.user_service.controller;

import com.hms.user_service.dto.request.PatientRegisterRequest;
import com.hms.user_service.dto.request.PatientUpdateRequest;
import com.hms.user_service.dto.response.PatientResponse;
import com.hms.user_service.exception.UnauthorizedException;
import com.hms.user_service.response.ApiResponse;
import com.hms.user_service.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<PatientResponse>> register(
            @Valid @RequestBody PatientRegisterRequest request) {
        PatientResponse response = patientService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Patient registered successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientById(
            @PathVariable Long id) {
        PatientResponse response = patientService.getPatientById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Patient retrieved successfully", response));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<PatientResponse>> getMyDetails(Authentication authentication) {
        Long authId = (Long) authentication.getCredentials();
        PatientResponse response = patientService.getPatientByAuthId(authId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Patient retrieved successfully", response));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<PatientResponse>> updateMyProfile(
            @RequestBody PatientUpdateRequest request,
            Authentication authentication) {
        Long authId = (Long) authentication.getCredentials();
        PatientResponse response = patientService.updatePatient(authId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Profile updated successfully", response));
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<?>> deleteMyAccount(Authentication authentication) {
        Long authId = (Long) authentication.getCredentials();
        patientService.deletePatient(authId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Account deleted successfully"));
    }
}

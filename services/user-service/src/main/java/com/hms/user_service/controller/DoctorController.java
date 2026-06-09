package com.hms.user_service.controller;

import com.hms.user_service.dto.request.DoctorApprovalRequest;
import com.hms.user_service.dto.request.DoctorRegisterRequest;
import com.hms.user_service.dto.request.DoctorUpdateRequest;
import com.hms.user_service.dto.response.DoctorResponse;
import com.hms.user_service.exception.UnauthorizedException;
import com.hms.user_service.response.ApiResponse;
import com.hms.user_service.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<DoctorResponse>> register(
            @Valid @RequestBody DoctorRegisterRequest request) {
        DoctorResponse response = doctorService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Doctor registered successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DoctorResponse>> getDoctorById(
            @PathVariable Long id) {
        DoctorResponse response = doctorService.getDoctorById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Doctor retrieved successfully", response));
    }

    @GetMapping("/auth/{authId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<DoctorResponse>> getDoctorByAuthId(@PathVariable Long authId) {
        DoctorResponse response = doctorService.getDoctorByAuthId(authId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Doctor retrieved successfully", response));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<DoctorResponse>> getMyDetails(Authentication authentication) {
        Long authId = (Long) authentication.getCredentials();
        DoctorResponse response = doctorService.getDoctorByAuthId(authId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Doctor retrieved successfully", response));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> getPendingDoctors() {
        List<DoctorResponse> response = doctorService.getPendingDoctors();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Pending doctors retrieved successfully", response));
    }

    @PutMapping("/approval/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DoctorResponse>> approveOrRejectDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorApprovalRequest request,
            Authentication authentication) {
        Long adminAuthId = (Long) authentication.getCredentials();
        DoctorResponse response = doctorService.approveOrRejectDoctor(id, request, adminAuthId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Doctor status updated successfully", response));
    }

    // Doctors can update their own profile
    @PutMapping("/me")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<DoctorResponse>> updateMyProfile(
            @RequestBody DoctorUpdateRequest request,
            Authentication authentication) {
        Long authId = (Long) authentication.getCredentials();
        DoctorResponse response = doctorService.updateDoctor(authId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Profile updated successfully", response));
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<?>> deleteMyAccount(Authentication authentication) {
        Long authId = (Long) authentication.getCredentials();
        doctorService.deleteDoctor(authId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Account deleted successfully"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> getAllDoctors() {
        List<DoctorResponse> response = doctorService.getAllDoctors();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Doctors retrieved successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctorById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Doctor deleted successfully"));
    }

    @GetMapping("/approved")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> getApprovedDoctors() {
        List<DoctorResponse> response = doctorService.getApprovedDoctors();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Approved doctors retrieved successfully", response));
    }
}

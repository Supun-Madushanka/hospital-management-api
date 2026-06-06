package com.hms.appointment_service.controller;

import com.hms.appointment_service.dto.request.AppointmentRequest;
import com.hms.appointment_service.dto.request.AppointmentStatusRequest;
import com.hms.appointment_service.dto.response.AppointmentResponse;
import com.hms.appointment_service.response.ApiResponse;
import com.hms.appointment_service.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> bookAppointment(
            @Valid @RequestBody AppointmentRequest request,
            Authentication authentication) {
        Long patientAuthId = (Long) authentication.getCredentials();
        AppointmentResponse response = appointmentService.bookAppointment(request, patientAuthId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Appointment booked successfully", response));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getMyAppointments(
            Authentication authentication) {
        Long authId = (Long) authentication.getCredentials();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("");
        List<AppointmentResponse> response = appointmentService.getMyAppointments(authId, role);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Appointments retrieved successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAllAppointments() {
        List<AppointmentResponse> response = appointmentService.getAllAppointments();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Appointments retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> getAppointmentById(
            @PathVariable Long id) {
        AppointmentResponse response = appointmentService.getAppointmentById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Appointment retrieved successfully", response));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateAppointmentStatus(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentStatusRequest request,
            Authentication authentication) {
        Long authId = (Long) authentication.getCredentials();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("");
        AppointmentResponse response = appointmentService.updateAppointmentStatus(id, request, authId, role);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Appointment status updated successfully", response));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> cancelAppointment(
            @PathVariable Long id,
            Authentication authentication) {
        Long authId = (Long) authentication.getCredentials();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("");
        AppointmentResponse response = appointmentService.cancelAppointment(id, authId, role);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Appointment cancelled successfully", response));
    }
}
package com.hms.user_service.controller;

import com.hms.user_service.dto.response.AdminResponse;
import com.hms.user_service.response.ApiResponse;
import com.hms.user_service.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminResponse>> getMyDetails(Authentication authentication) {
        Long authId = (Long) authentication.getCredentials();
        AdminResponse response = adminService.getAdminByAuthId(authId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Admin retrieved successfully", response));
    }
}

package com.hms.user_service.service;

import com.hms.user_service.dto.response.AdminResponse;
import com.hms.user_service.entity.Admin;
import com.hms.user_service.exception.ResourceNotFoundException;
import com.hms.user_service.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;

    @Override
    public AdminResponse getAdminByAuthId(Long authId) {
        Admin admin = adminRepository.findByAuthId(authId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        return mapToResponse(admin);
    }

    private AdminResponse mapToResponse(Admin admin) {
        return AdminResponse.builder()
                .id(admin.getId())
                .authId(admin.getAuthId())
                .name(admin.getName())
                .email(admin.getEmail())
                .phone(admin.getPhone())
                .createdAt(admin.getCreatedAt())
                .updatedAt(admin.getUpdatedAt())
                .build();
    }
}

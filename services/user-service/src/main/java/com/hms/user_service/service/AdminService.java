package com.hms.user_service.service;

import com.hms.user_service.dto.response.AdminResponse;

public interface AdminService {
    AdminResponse getAdminByAuthId(Long authId);
}

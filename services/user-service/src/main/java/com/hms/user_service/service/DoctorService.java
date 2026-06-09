package com.hms.user_service.service;

import com.hms.user_service.dto.request.DoctorApprovalRequest;
import com.hms.user_service.dto.request.DoctorRegisterRequest;
import com.hms.user_service.dto.request.DoctorUpdateRequest;
import com.hms.user_service.dto.response.DoctorResponse;
import java.util.List;

public interface DoctorService {
    DoctorResponse register(DoctorRegisterRequest request);
    DoctorResponse getDoctorById(Long id);
    DoctorResponse getDoctorByAuthId(Long authId);
    List<DoctorResponse> getPendingDoctors();
    DoctorResponse approveOrRejectDoctor(Long id, DoctorApprovalRequest request, Long adminAuthId);
    DoctorResponse updateDoctor(Long authId, DoctorUpdateRequest request);
    void deleteDoctor(Long authId);
    List<DoctorResponse> getAllDoctors();
    void deleteDoctorById(Long id);
    List<DoctorResponse> getApprovedDoctors();
}

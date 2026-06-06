package com.hms.user_service.service;

import com.hms.user_service.dto.request.PatientRegisterRequest;
import com.hms.user_service.dto.response.PatientResponse;

public interface PatientService {
    PatientResponse register(PatientRegisterRequest request);
    PatientResponse getPatientById(Long id);
    PatientResponse getPatientByAuthId(Long authId);
}
